# JavaRun
JAVA 8 程序在线运行环境

## 预览地址
https://java.rawchen.com


## 开发环境
SpringBoot 2.5.5 + Thymeleaf

## 运行截图
![](https://cdn.jsdelivr.net/gh/rawchen/JsDelivr/static/JavaRun/00.png)

## 开发想法

一开始的思路是两个步骤：
1. 从程序中调用Java编程语言编译器的接口编译出class文件
2. 通过Class的getMethod再调用invoke方法，用了线程池

后面发现简单的java操作可实现，多线程有问题。
所以第2步思路换成了Runtime的exec去执行java Main

### 过程难点：
1. 流重定向问题，包括process的异常、输入流
2. Runtime的exec问题
3. 处理各种异常和错误

### 使用说明
如需部署，请自行下载`monaco-editor`到`static`文件夹。
并更改`cdn.rawchen.com/vs`为`../static/monaco-editor/vs`。
下载地址：[https://microsoft.github.io/monaco-editor](https://microsoft.github.io/monaco-editor)
<br>
解压出/package/min/vs即可。