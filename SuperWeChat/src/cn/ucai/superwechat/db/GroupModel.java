package cn.ucai.superwechat.db;

import android.content.Context;

import java.io.File;

import cn.ucai.superwechat.I;
import cn.ucai.superwechat.utils.OkHttpUtils;

/**
 * Created by w on 2017/4/10.
 */

public class GroupModel implements IGroupModel {
    @Override
    public void newGroup(Context context, String hxid, String groupName, String description,
                         String owner, boolean isPublic, boolean isInvites, File file,
                         OnCompleteListener<String> listener) {
        OkHttpUtils<String> utils = new OkHttpUtils<>(context);
        utils.setRequestUrl(I.REQUEST_CREATE_GROUP)
                .addParam(I.Group.HX_ID, hxid)
                .addParam(I.Group.NAME,groupName)
                .addParam(I.Group.DESCRIPTION,description)
                .addParam(I.Group.OWNER,owner)
                .addParam(I.Group.IS_PUBLIC,String.valueOf(isPublic))
                .addParam(I.Group.ALLOW_INVITES,String.valueOf(isInvites))
                .addFile2(file)
                .targetClass(String.class)
                .post()
                .execute(listener);
    }
}
