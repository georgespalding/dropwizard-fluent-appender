package io.github.dropwizard.logging.fluent;

import ch.qos.logback.core.UnsynchronizedAppenderBase;
import ch.qos.logback.core.net.DefaultSocketConnector;
import ch.qos.logback.core.net.SocketConnector;
import ch.qos.logback.core.util.CloseUtil;

import javax.net.SocketFactory;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public abstract class FluentBaseAppender<E, VO> extends UnsynchronizedAppenderBase<E> {

   private final int port;
   private final String peerId;
   private final InetAddress address;
   private final long reconnectionDelayMillis;
   private final int acceptConnectionTimeoutMillis;
   private final FluentEncoder encoder;
   private SocketConnector connector;
   private Socket socket;
   private OutputStream out;

   public FluentBaseAppender(
      String host,
      int port,
      long reconnectionDelayMillis,
      int acceptConnectionTimeoutMillis,
      FluentEncoder encoder
   ) {
      this.port = port;
      this.reconnectionDelayMillis = reconnectionDelayMillis;
      this.acceptConnectionTimeoutMillis = acceptConnectionTimeoutMillis;
      this.encoder = encoder;

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
         final VO data = transform(event);
         out.write(encoder.encodeFluentEvent(getTimeStamp(data), data));
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

   protected abstract long getTimeStamp(VO data);

   protected abstract VO transform(E event);

}