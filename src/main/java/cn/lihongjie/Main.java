package cn.lihongjie;

import io.vavr.API;
import io.vavr.Tuple;
import lombok.SneakyThrows;
import net.jpountz.lz4.LZ4BlockOutputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.apache.commons.compress.compressors.lzma.LZMACompressorOutputStream;
import org.apache.commons.compress.compressors.snappy.FramedSnappyCompressorOutputStream;
import org.apache.commons.compress.compressors.xz.XZCompressorOutputStream;
import org.apache.commons.compress.compressors.zstandard.ZstdCompressorOutputStream;
import org.apache.commons.io.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.function.Function;
import java.util.zip.DeflaterOutputStream;

public class Main {


    @SneakyThrows
    public static void main(String[] args) {

        byte[] json1MB = Files.readAllBytes(Paths.get("./data/1mb.json"));
        byte[] json5MB = Files.readAllBytes(Paths.get("./data/5mb.json"));
        byte[] json10MB = Files.readAllBytes(Paths.get("./data/10mb.json"));
        byte[] json20MB = Files.readAllBytes(Paths.get("./data/20mb.json"));


        var algList = Arrays.asList(

                Tuple.of("bzip2", API.<OutputStream, OutputStream>unchecked(is -> new BZip2CompressorOutputStream(is))),
                Tuple.of("gzip", API.<OutputStream, OutputStream>unchecked(is -> new GzipCompressorOutputStream(is))),
                Tuple.of("lzma", API.<OutputStream, OutputStream>unchecked(is -> new LZMACompressorOutputStream(is))),
                Tuple.of("xz", API.<OutputStream, OutputStream>unchecked(is -> new XZCompressorOutputStream(is))),
                Tuple.of("zstd", API.<OutputStream, OutputStream>unchecked(is -> new ZstdCompressorOutputStream(is))),
                Tuple.of("DEFLATE", API.<OutputStream, OutputStream>unchecked(is -> new DeflaterOutputStream(is))),
//                Tuple.of("LZ4_apache", API.<OutputStream, OutputStream>unchecked(is -> new BlockLZ4CompressorOutputStream(is))),
                Tuple.of("LZ4_jpountz", API.<OutputStream, OutputStream>unchecked(is -> new LZ4BlockOutputStream(is))),
                Tuple.of("Snappy", API.<OutputStream, OutputStream>unchecked(is -> new FramedSnappyCompressorOutputStream(is)))


        );

        System.out.printf("%-5s\t%-10s\t%-12s\t%-12s\t%-11s\t%-12s\n", "filename", "alg", "rawSize", "compressedSize", "ratio", "time");


        algList.forEach(alg -> {
            try {
                doCompression(json1MB, "1MB", alg._1, alg._2);
                doCompression(json5MB, "5MB", alg._1, alg._2);
                doCompression(json10MB, "10MB", alg._1, alg._2);
                doCompression(json20MB, "20MB", alg._1, alg._2);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });


    }


    private static void doCompression(byte[] data, String prefix, String compressorName, Function<OutputStream, OutputStream> compressor) throws IOException {
        ByteArrayOutputStream bas = new ByteArrayOutputStream();


        long start = System.currentTimeMillis();

        OutputStream os = compressor.apply(bas);


        IOUtils.copy(new ByteArrayInputStream(data), os);


        long end = System.currentTimeMillis();


        int dataSize = data.length;

        double dataSizeMB = dataSize / 1024.0 / 1024.0;

        int compressedSize = bas.size();

        double compressedSizeMB = compressedSize / 1024.0 / 1024.0;

        double ratio = new BigDecimal((double) compressedSize / dataSize).multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP).doubleValue();

        System.out.printf("%5s\t%10s\t%10.2fMB\t%10.2fMB\t%10.2f%%\t%10dms\n", prefix, compressorName, dataSizeMB, compressedSizeMB, ratio, end - start);
    }
}