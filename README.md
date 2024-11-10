

## 背景
考虑到项目中经常把JSON字符串存储到数据库和Redis缓存中,使用压缩算法可以有效的减少存储空间的需求. 本文使用[Apache Commons Compress](https://commons.apache.org/proper/commons-compress/) 作为研究对象, 对比不同的压缩算法的压缩时间和压缩比例, 为后续项目JSON压缩算法选择提供数据基础.





## 测试及结果


使用压缩算法分别 1MB 5MB  10MB 20MB 4种规格的JSON文件进行压缩, 计算出压缩比以及耗时.



备注:

1. 所有算法都采用默认配置,.
2. [Apache Commons Compress](https://commons.apache.org/proper/commons-compress/) 中的LZ4实现压缩特别耗时,原因未排查, 使用net.jpountz.lz4 替代.
3. 耗时只记录了一次, 不太严谨, 应该使用JMH进行测试.





```java
filename	alg       	rawSize     	compressedSize	ratio      	time        
  1MB	     bzip2	      1.10MB	      0.16MB	     14.55%	       218ms
  5MB	     bzip2	      5.01MB	      0.80MB	     15.95%	       546ms
 10MB	     bzip2	      9.92MB	      1.76MB	     17.73%	      1064ms
 20MB	     bzip2	     19.65MB	      3.52MB	     17.89%	      2018ms
  1MB	      gzip	      1.10MB	      0.32MB	     29.14%	        27ms
  5MB	      gzip	      5.01MB	      1.53MB	     30.52%	       108ms
 10MB	      gzip	      9.92MB	      3.06MB	     30.83%	       214ms
 20MB	      gzip	     19.65MB	      6.07MB	     30.89%	       431ms
  1MB	      lzma	      1.10MB	      0.25MB	     23.14%	       487ms
  5MB	      lzma	      5.01MB	      1.10MB	     21.94%	      2190ms
 10MB	      lzma	      9.92MB	      2.13MB	     21.51%	      4959ms
 20MB	      lzma	     19.65MB	      4.18MB	     21.27%	     10938ms
  1MB	        xz	      1.10MB	      0.25MB	     22.79%	       403ms
  5MB	        xz	      5.01MB	      1.06MB	     21.21%	      2240ms
 10MB	        xz	      9.92MB	      2.12MB	     21.42%	      5001ms
 20MB	        xz	     19.65MB	      4.12MB	     20.99%	     10931ms
  1MB	      zstd	      1.10MB	      0.29MB	     26.06%	        64ms
  5MB	      zstd	      5.01MB	      1.41MB	     28.16%	        21ms
 10MB	      zstd	      9.92MB	      2.78MB	     28.02%	        41ms
 20MB	      zstd	     19.65MB	      5.52MB	     28.12%	        82ms
  1MB	   DEFLATE	      1.10MB	      0.32MB	     29.14%	        24ms
  5MB	   DEFLATE	      5.01MB	      1.53MB	     30.52%	       109ms
 10MB	   DEFLATE	      9.92MB	      3.06MB	     30.83%	       215ms
 20MB	   DEFLATE	     19.65MB	      6.07MB	     30.89%	       428ms
  1MB	LZ4_jpountz	      1.10MB	      0.49MB	     44.65%	        28ms
  5MB	LZ4_jpountz	      5.01MB	      2.30MB	     45.98%	        10ms
 10MB	LZ4_jpountz	      9.92MB	      4.54MB	     45.82%	        20ms
 20MB	LZ4_jpountz	     19.65MB	      9.04MB	     45.99%	        36ms
  1MB	    Snappy	      1.10MB	      0.47MB	     43.25%	        67ms
  5MB	    Snappy	      5.01MB	      2.23MB	     44.58%	       175ms
 10MB	    Snappy	      9.92MB	      4.40MB	     44.40%	       339ms
 20MB	    Snappy	     19.65MB	      8.76MB	     44.58%	       668ms
```



## 结论
1. 如果追求压缩比, 使用 bzip2, 比例在15%左右
2. 如果追求压缩时间, 使用 zstd, 比例为30%以内, 耗时不超过80ms.







