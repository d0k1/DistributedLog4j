This project demonstrates various way to get distributed logs with log4j.

There you can find examples of using udp as transport by netty, tcp as transport by netty, jgroups as transport to make logs distributed.

Every transport example consists of appender for log4j and liistener. Listener's taks is to get log4j logging event and store them using specified appender.

You can use these examples whatever you want, according the MIT License.
