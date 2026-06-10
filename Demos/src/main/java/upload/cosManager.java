package upload;

import java.io.InputStream;

//腾讯云对象存储操作类
@Component
public class cosManager {
    //客户端配置类，从application.yml中读取配置，有cosClent的创建方法
    @Resource
    private CosClientConfig cosClientConfig;

    @Resoucrce
    private CosClient cosClient;

    //上传图片到cos
    public UploadPictureResult putObject(String key,File file){
        return cosClient.putObject(new PutObjectRequest(cosClientConfig.getBucket(),key,file));
    }
    //从cos下载图片
    public COSObject getObject(String key){
        return cosClient.getObject(new GetObjectRequest(cosClientConfig.getBucket(),key));
    }

    //上传并处理图片
    public PutObjectResult putPictureObject(String key,File file){
        //创建上传请求
        PutObjectRequest putObjectRequest = new PutObjectRequest(cosClientConfig.getBucket(), key, file);
        //给上传请求添加图片处理规则
        putObjectRequest.setPicOperations(picOperations);
        //上传
        return cosClient.putObject(putObjectRequest);
    }

    //流式上传图片到cos
    public PutObjectResult putObject(String key, InputStream inputStream, long size, String contentType) {
        //创建流式图片的数据元信息。包括大小和数据类型
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(size);
        objectMetadata.setContentType(contentType);
        //流式上传
        PutObjectRequest putObjectRequest = new PutObjectRequest(cosClientConfig.getBucket(), key, inputStream, objectMetadata);
        return cosClient.putObject(putObjectRequest);
    }

    //流式上传并处理图片
    public PutObjectResult putPictureObject(String key, InputStream inputStream,long size,String contentType){
        //创建图片元信息
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(size);
        objectMetadata.setContentType(contentType);
        //创建图片上传请求
        PutObjectRequest putObjectRequest = new PutObjectRequest(cosClientConfig.getBucket(), key, inputStream, objectMetadata);
        //设置图片处理规则
        putObjectRequest.setPicOperations(buildPicOperations(key, size));
        //流式上传
        return cosClient.putObject(putObjectRequest);
    }

    //删除图片
    public void deleteObject(String key){
        cosClient.deleteObject(cosClientConfig.getBucket(),key);
    }
}
