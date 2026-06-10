package upload;

import java.io.File;
import java.io.IOException;

@Slf4j
public abstract class PictureUploadTemplate {

    @Resource
    private CosManager cosManager;
    @Resource
    private CosClientConfig cosClientConfig;
    @Resource
    private CosClient cosClient;

    //图片上传模板方法
    public abstract PutObjectResult uploadPicture(Object inputSource,String uploadPathPrefix){
        //todo 1.校验图片参数
        validPicture();
        //2.确定上传相对路径 完整路径----eg:public/userId/2026-04-23_UUID.jpg
        //上传文件名称：年月日_UUID.原始文件名的后缀   2026-04-23_UUID.jpg
        // 完整路径=前缀+上传文件名   外部传入
        String uuid = RandomUtil.randomString(16);
        //todo 根据不同输入源获取图片原始名称
        String originalFilename = getOriginFilename();
        String uploadFilename=String.format("%s_%s.%s",DateUtil.formatDate(new Date()),uuid,FileUtil.getSuffix(originalFilename));
        String uploadPath=String.format("%s/%s",uploadPathPrefix,uploadFilename)
        //3.上传图片
        File file=null;
            file= File.createTempFile(originalFilename,null);
            //todo 下载图片
            processFile(inputSource,file);
            //上传图片
            PutObjectResult putObjectResult=cosManager.putPictureObject(uploadPath,file);
            return putObjectResult;
            //记录cos处理结果
            return buildResult(imageInfo, uploadPath, file, originalFilename);
         finally {
            //4.删除临时文件
            cosManager.deleteObject(uploadPath);
        }
    }

    public abstract void validPicture(Object inputSource);

    public abstract String getOriginFilename(Object inputSource);

    public abstract void processFile(Object inputSource, File file) throws IOException;
}
