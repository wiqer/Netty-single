# Netty-single
Netty single thread
## 单线程版本的Netty 
修改了Netty的线程模型，netty的线程改为affinity

单线程绑定单端口，一个线程处理io和业务，线程绑定到固定的cpu上，没有线程上下文切换

