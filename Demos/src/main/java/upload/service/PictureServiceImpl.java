package upload.service;

@Service
@Slf4j
public class PictureServiceImpl extends ServiceImpl<PictureMapper, Picture>
        implements PictureService{

    @Resource
    XXXXX

    //上传图片
    @Override
    public PictureVo uploadPicture(Object inputResource, PictureUploadRequest pictureUploadRequest, User loginUser) {
        //1.校验权限
        if(pictureUploadRequest.getId()!=null){
            //判断是否存在这个空间
            Space space = spaceService.getById(spaceId);
            ThrowUtils.throwIf(space == null, ErrorCode.NOT_FOUND_ERROR, "没有这个空间");
            //判断剩余空间额度是否足够
            if (space.getTotalCount() > space.getMaxCount()) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "空间中图片的数量超出");
            }
            if (space.getTotalSize() > space.getMaxSize()) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "空间中图片的大小超出");
            }
        }
        //2.上传或更新图片
        UploadPictureResult uploadPictureResult = pictureUploadTemplate.uploadPicture(inputResource, uploadPathPrefix);
        //3.更新容量
        Long finalSpaceId=spaceId;
        transactionTemplate.execute(status->{
            //上传或更新
            boolean saveOrUpdate=saveOrUpdate(picture);
            ThrowUtils.throwIf(!saveOrUpdate,ErrorCode.OPERATION_ERROR,"上传图片失败");
            //如果上传到空间中就要更新空间额度
            if(finalSpaceId!=null){
                boolean update=spaceService.lambdaUpdate()
                        .eq(Space::getId,finalSpaceId)
                        .setSql("totalSize=totalSize+"+picture.getPicSize)
                        .setSql("totalCount=totalCount+1")
                        .update();
                ThrowUtils.throwIf(!update....)
            }
            return picture;
        });
        //4.删除被替换掉的图片
        if(pictureId!=null  && oldPicture !=null){
            this.clearPictureFile(oldPicture);
        }
    }

    //删除图片
    @Override
    public void deletePicture(Long pictureId, User loginUser) {
        //1.校验权限
        //有图片才可以删除
        Picture oldPicture = getById(pictureId);
        ThrowUtils.throwIf(!oldPicture.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser), ErrorCode.NOT_FOUND_ERROR);
        //2.删除图片，更新额度
        transactionTemplate.execute(status->{
            boolean remove = removeById(pictureId);
            ThrowUtils.throwIf(!remove, ErrorCode.OPERATION_ERROR, "删除图片失败");
            //如果图片在空间中还要更新空间额度
            if(finalSpaceId!=null){
                boolean update=spaceService.lambdaUpdate()
                        .eq(Space::getId,oldPicture.getSpaceId())
                        .setSql("totalSize=totalSize-"+oldPicture.getPicSize)
                        .setSql("totalCount=totalCount-1")
                        .update();
                ThrowUtils.throwIf(!update,ErrorCode.OPERATION_ERROR,"更新空间额度失败");
            }
            return true;
        });
        //3.删除cos图片
        clearPictureFile(oldPicture);
    }
}
