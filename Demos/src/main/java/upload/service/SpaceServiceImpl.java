package upload.service;

@Service
public class SpaceServiceImpl extends  ServiceImpl<SpaceMapper,Space> implements SpaceService{

    @Resource
            ......

    //创建空间
    @Override
    public long addSpace(SpaceAddRequest spaceAddRequest,User user){
        //1.补充参数
        //2。校验请求参数
        //3.校验权限
        //4.控制每个用户只能创建一个私有空间和团队空间
        String lock=String.valueOf(user.getId()).intern();
        synchronized (lock){
           Long spaceId=transactionTemplate.execute(status->{
               //判断空间是否存在
               boolean exist = this.lambdaQuery()
                       .eq(Space::getUserId, user.getId())
                       .eq(Space::getSpaceType, spaceAddRequest.getSpaceType())
                       .exists();
               if (exist) {
                   throw new BusinessException(ErrorCode.OPERATION_ERROR, "用户已经创建过空间");
               }
               //不存在则创建
               boolean result=this.save(space);
               ThrowUtils.throwIf(!result,ErrorCode.OPERATION_ERROR,"创建空间失败");
               //如果是团队空间，还需要创建团队成员，把当前用户作为管理员加入SpaceUser表
               return space.getId();
           });
           return spaceId==null?-1:spaceId;
        }
    }


}
