package io.github.dropwizard.logging.fluent;

import static java.lang.Math.toIntExact;

import ch.qos.logback.core.UnsynchronizedAppenderBase;
import ch.qos.logback.core.net.DefaultSocketConnector;
import ch.qos.logback.core.net.SocketConnector;
import ch.qos.logback.core.util.CloseUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.msgpack.core.MessageBufferPacker;
import org.msgpack.core.MessagePack;
import org.msgpack.jackson.dataformat.MessagePackFactory;
import org.msgpack.value.impl.ImmutableExtensionValueImpl;

import javax.net.SocketFactory;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

public abstract class FluentBaseAppender<E, VO> extends UnsynchronizedAppenderBase<E> {

   protected final BigInteger ONE_MILLION = BigInteger.TEN.pow(6);
   protected final ObjectMapper msgpackMapper = new ObjectMapper(new MessagePackFactory());
   private final String peerId;
   private final InetAddress address;
   private final int port;
   private final long reconnectionDelayMillis;
   private final int acceptConnectionTimeoutMillis;
   private final String tag;
   private SocketConnector connector;
   private Socket socket;
   private OutputStream out;

   public FluentBaseAppender(
      String host,
      int port,
      String tag,
      long reconnectionDelayMillis,
      int acceptConnectionTimeoutMillis
   ) {
      this.port = port;
      this.tag = tag;
      this.reconnectionDelayMillis = reconnectionDelayMillis;
      this.acceptConnectionTimeoutMillis = acceptConnectionTimeoutMillis;

      int errorCount = 0;
      if (port <= 0) {
         errorCount++;
         addError("No port was configured for appender" + name + " For more information, please visit http://logback.qos.ch/codes.html#socket_no_port");
      }

      if (host == null) {
         errorCount++;
         addError("No remote host was configured for appender" + name
                     + " For more information, please visit http://logback.qos.ch/codes.html#socket_no_host");
      }

      this.peerId = "remote peer " + host + ":" + port + ": ";
      if (errorCount == 0) {
         try {
            address = InetAddress.getByName(host);
         } catch (UnknownHostException e) {
            addError("unknown host: " + host);
            throw new RuntimeException("unknown host: " + host, e);
         }
      } else {
         throw new RuntimeException(peerId + "invalid connection details");
      }
   }

   @Override
   protected void append(E event) {
      while (out == null) {
         optimisticConnect();
      }
      try {
         out.write(encode(transform(event)));
      } catch (IOException e) {
         closeSocket();
      }
   }

   @Override
   public void start() {
      if (isStarted()) {
         return;
      }

      connector = newConnector(address, port, 0, reconnectionDelayMillis);
      connector.setExceptionHandler((connector, ex) -> {
         if (ex instanceof InterruptedException) {
            addInfo("connector interrupted");
         } else if (ex instanceof ConnectException) {
            addInfo(peerId + "connection refused");
         } else {
            addInfo(peerId + ex);
         }
      });
      connector.setSocketFactory(getSocketFactory());

      optimisticConnect();
      super.start();
   }

   @Override
   public void stop() {
      if (!isStarted()) {
         return;
      }
      closeSocket();
      super.stop();
   }

   private void optimisticConnect() {
      try {
         addInfo(peerId + "attempt to connect");
         socket = connector.call();
         socket.setSoTimeout(acceptConnectionTimeoutMillis);
         out = socket.getOutputStream();
         socket.setSoTimeout(0);
         addInfo(peerId + "connection established");
      } catch (InterruptedException | IOException e) {
         // Optimistic, connections might start working later
         addInfo(peerId + "connection attempt failed: " + e);
         closeSocket();
      }
   }

   private void closeSocket() {
      CloseUtil.closeQuietly(socket);
      socket = null;
      out = null;
      addInfo(peerId + "connection closed");
   }

   /**
    * Creates a new {@link SocketConnector}.
    * <p>
    * The default implementation creates an instance of {@link DefaultSocketConnector}.
    * A subclass may override to provide a different {@link SocketConnector}
    * implementation.
    *
    * @param address      target remote address
    * @param port         target remote port
    * @param initialDelay delay before the first connection attempt
    * @param retryDelay   delay before a reconnection attempt
    *
    * @return socket connector
    */
   protected SocketConnector newConnector(InetAddress address, int port, long initialDelay, long retryDelay) {
      return new DefaultSocketConnector(address, port, initialDelay, retryDelay);
   }

   /**
    * Gets the default {@link SocketFactory} for the platform.
    * <p>
    * Subclasses may override to provide a custom socket factory.
    */
   protected SocketFactory getSocketFactory() {
      return SocketFactory.getDefault();
   }

   protected byte[] encode(VO event) throws IOException {
      byte[] tagAndTimestamp = packTagAndTimestamp(getTimeStamp(event));
      byte[] data = packData(event);

      // Join the first two and the third together
      byte[] combined = new byte[tagAndTimestamp.length + data.length];

      System.arraycopy(tagAndTimestamp, 0, combined, 0, tagAndTimestamp.length);
      System.arraycopy(data, 0, combined, tagAndTimestamp.length, data.length);
      return combined;
   }

   protected byte[] packTagAndTimestamp(long timestamp) throws IOException {
      // FIXME having issues encoding EventTime in the desired format for fluent protocol v1
      // See https://github.com/fluent/fluentd/wiki/Forward-Protocol-Specification-v0#eventtime-ext-format
      // epoch ms. multiply by 10^6 to get ns.
      // toLongExact Will carp if there is an overflow (which will happen after 2262-04-11T23:47:16.854Z).
//      final long epochLong = BigInteger.valueOf(timestamp).multiply(ONE_MILLION).longValueExact();
//      final byte[] epochNanosBuffer = ByteBuffer.allocate(Long.BYTES).putLong(epochLong).array();

      final MessageBufferPacker packer = MessagePack.newDefaultBufferPacker();
      packer
         .packArrayHeader(3)
         .packString(tag)
         .packInt(toIntExact(timestamp/1000));
         // For fluent protocol v0:
         //.packValue(new ImmutableExtensionValueImpl((byte)0, epochNanosBuffer));
         // For fluent protocol v1:
         // .addPayload(epochNanosBuffer);
         // .packExtensionTypeHeader((byte) 0, Long.BYTES)
      packer.close();
      return packer.toByteArray();
   }

   protected abstract long getTimeStamp(VO data);

   protected abstract byte[] packData(VO data) throws JsonProcessingException;

   protected abstract VO transform(E event);

}