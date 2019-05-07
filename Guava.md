# Guava

Guava是一组核心库，包括新的集合类型（例如multimap和multiset），不可变集合，图形库，函数类型，内存缓存以及用于并发，
I/O，散列，基元的API /实用程序 ，反射，字符串处理等等！

源码地址 [google/guava](https://github.com/google/guava)  
官方教程 [User Guide](https://github.com/google/guava/wiki)  
使用案例 [Google Guava](https://www.tfnico.com/presentations/google-guava)  

## Caches缓存

当值计算或检索的代价很高时应该考虑使用缓存。
Guava Cache 实现的是应用的本地缓存。

Caches和ConcurrentMap类似，但是Caches通常有自动回收元素的功能用来限制内存占用。
但是LoadingCache是特例不会自动逐出内容但是有自己的自动缓存加载机制。

适用场景：  
你愿意消耗一些内存空间来提升速度。  
你预料到某些键会被查询一次以上。  
缓存中存放的数据总量不会超出内存容量。  

### Cache实现

#### Cache类型

+ LoadingCache

#### Cache构建

+ CacheLoader

#### 缓存回收方式

+ 基于容量回收

+ 定时回收

+ 基于引用回收

+ 显示清除

#### 移除监听

#### 刷新键值

## Guava发布/订阅（观察者）模式

![](https://upload-images.jianshu.io/upload_images/10517880-05a3b5c7ffa8870b.jpg?imageMogr2/auto-orient/)

发布订阅相关框架：  
RxJava2.0  
消息队列中间件（Kafka、RocketMQ、RabbitMQ）  
Redis也有Pub/Sub模式  
Hazelcast  

JDK中也有观察者模式的默认实现Observable和Observer。

TODO：比较一下他们的实现与优缺。

### 使用场景

单块架构进程内异步通信，如：  
1）数据到来后要同时入库以及报警，报警要向三个不同的终端发送消息。
![](https://upload-images.jianshu.io/upload_images/10517880-7b85ee18b520bc1e..jpeg?imageMogr2/auto-orient/)

分布式架构节点之间不能使用。

### EventBus事件发布订阅编程步骤

1）创建事件总线  
2）创建事件订阅者  
    2.1）实现事件发布后触发的回调函数  
3）将事件订阅者注册到事件总线  
4）发布事件，事件可以是任何类型（？extends Object）

具体参考ns-core模块的测试代码。

#### EventBus

+ EventBus

+ AsyncEventBus

    AsyncEventBus与EventBus的不同在于事件队列，AsyncEventBus中使用ConcurrentLinkedQueue代替EventBus的ThreadLocal；
    AsyncEventBus消费事件则是通过一个线程池（Executor）。
    
    ```
    //由下面源码可知,执行消费回调方法必需通过Executor的execute()方法。
    //com/google/common/eventbus/Subscriber.java
    final void dispatchEvent(final Object event) {
        this.executor.execute(new Runnable() {
            public void run() {
                try {
                    Subscriber.this.invokeSubscriberMethod(event);
                } catch (InvocationTargetException var2) {
                    Subscriber.this.bus.handleSubscriberException(var2.getCause(), Subscriber.this.context(event));
                }
            }
        });
    }
    ```
    
#### 源码实现分析

### 测试Demo

学习Guava的使用方法，看Guava源码的测试代码是

参考github上测试源码：  
guava/guava-tests/benchmark/com/google/common/eventbus/  
guava/guava-tests/test/com/google/common/eventbus/  

TODO：  
为何Guava源码中测试，使用junit.framework.*, 测试类继承TestCase，
而不是使用org.junit.* 及 @Test，@Before这些？为了兼容性么？还是其他什么原因？
