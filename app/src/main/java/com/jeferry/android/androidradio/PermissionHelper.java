package com.jeferry.android.androidradio;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;


import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;


import java.util.ArrayList;
import java.util.List;


/**
 * @author mzchen
 * @date 2019/9/9 17:37
 * @des 权限申请帮助类
 * step1 ：实例化 PermissionHelper
 * step2 ：Activity fragment 里面
 * <p>
 * help.onRequestPermissionsResult(requestCode, permissions, grantResults);
 * help.mPermissionHelper.onActivityResult(requestCode, resultCode, data);
 * </p>
 * @mail chenmingzhi@ccclubs.com
 */
public class PermissionHelper {

  /*  private static final int MY_PERMISSIONS_REQUEST = 1001;

    private static final int REQUEST_CODE_REQUEST_SETTING = 1027;

    private List<String> needPermissions = null;

    private Action mPermissionAction;

    private Action mPermissionDenyAction;

    private Activity mActivity;

    private Fragment mFragment;

    private String mPermissionDescription;

    public boolean isToSettingForPermission;

    public PermissionHelper(@NonNull final FragmentActivity activity) {
        mActivity = activity;
    }

    public PermissionHelper(@NonNull final Fragment fragment) {
        mFragment = fragment;
    }

    *//**
     * @param permissions               权限列表
     * @param action                    获得权限之后的回调
     * @param needPermissionDescription 需要这些权限的原因（当用户第一次拒绝之后，会提示这个原因）
     *//*
    public void requestPermission(String[] permissions, Action action, Action denyAction, String needPermissionDescription) {
        if (mActivity == null && null == mFragment) {
            throw new RuntimeException("u must set a fragment or activity");
        }
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(null != mActivity ? mActivity : mFragment.getActivity(), permission) != PackageManager.PERMISSION_GRANTED) {
                if (needPermissions == null) {
                    needPermissions = new ArrayList<>();
                }
                needPermissions.add(permission);
            }
        }
        // 已经获得权限，直接执行
        if (needPermissions == null) {
            if (action != null) {
                try {
                    action.run();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return;
        }
        // 是否需要权限的原因。当申请的权限被用户拒绝之后，再次申请时会提示
        boolean needRationale = false;
        for (String p : needPermissions) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(null != mActivity ? mActivity : mFragment.getActivity(), p)) {
                needRationale = true;
                break;
            }
        }
        // 获得权限之后的回调
        mPermissionAction = action;
        //权限拒绝后的回调
        mPermissionDenyAction = denyAction;
        // 申请权限的描述信息
        mPermissionDescription = needPermissionDescription;
        // 提示之后申请权限
        if (needRationale) {
            new ExSweetAlertDialog(null != mActivity ? mActivity : mFragment.getActivity(), ExSweetAlertDialog.AlertDialogType.NORMAL_TYPE)
                    .setTitleText("权限申请")
                    .setContentText(needPermissionDescription)
                    .setConfirmClickListener(dialog -> {
                        applyPermission(needPermissions);
                        dialog.dismissWithAnimation();
                    }).show();
        }
        // 直接申请权限
        else {
            applyPermission(needPermissions);
        }
    }

    private void applyPermission(List<String> needPermission) {
        if (null != mActivity) {
            ActivityCompat.requestPermissions(mActivity, needPermission.toArray(new String[0]), MY_PERMISSIONS_REQUEST);
        } else if (null != mFragment) {
            mFragment.requestPermissions(needPermission.toArray(new String[0]), MY_PERMISSIONS_REQUEST);
        }
    }


    *//**
     * 在 activity 或者 fragment {onActivityResult}里面调用
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     *//*
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        KLog.d((null != mActivity ? mActivity : mFragment).getClass().getSimpleName() + " ; onRequestPermissionsResult ");
        if (requestCode == MY_PERMISSIONS_REQUEST) {
            boolean allGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                }
            }
            // 已获得全部权限
            if (allGranted) {
                if (mPermissionAction != null) {
                    try {
                        mPermissionAction.run();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            // 未获得权限，提示是否打开设置页面
            else {
                new ExSweetAlertDialog(null != mActivity ? mActivity : mFragment.getActivity(), ExSweetAlertDialog.AlertDialogType.ERROR_TYPE)
                        .setTitleText("权限申请失败")
                        .setContentText(mPermissionDescription + "\n是否前往设置？")
                        .setCancelText("取消")
                        .setConfirmText("确认")
                        .setConfirmClickListener(dialog -> {
                            isToSettingForPermission = true;
                            startAppSettings();
                            dialog.dismissWithAnimation();
                        })
                        .setCancelClickListener(dialog -> {
                            if (mPermissionDenyAction != null) {
                                try {
                                    mPermissionDenyAction.run();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            dialog.dismissWithAnimation();
                        }).show();
            }
            return;
        }
    }

    *//**
     * 在 activity 或者 fragment {onActivityResult}里面调用
     *
     * @param requestCode
     * @param resultCode
     * @param data
     *//*
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        KLog.d((null != mActivity ? mActivity : mFragment).getClass().getSimpleName() + " ; onActivityResult ");
        if (requestCode == REQUEST_CODE_REQUEST_SETTING) {
            List<String> list = new ArrayList<>();
            for (String permission : needPermissions) {
                if (ContextCompat.checkSelfPermission(null != mActivity ? mActivity : mFragment.getActivity(), permission) == PackageManager.PERMISSION_DENIED) {
                    list.add(permission);
                }
            }
            if (list.size() > 0) {
                onRequestPermissionFailed(list.toArray(new String[list.size()]));
            } else {
                if (mPermissionAction != null) {
                    try {
                        mPermissionAction.run();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public void startAppSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + (null != mActivity ? mActivity : mFragment.getActivity()).getPackageName()));
        if (null != mActivity) {
            mActivity.startActivityForResult(intent, REQUEST_CODE_REQUEST_SETTING);
        } else {
            mFragment.startActivityForResult(intent, REQUEST_CODE_REQUEST_SETTING);
        }
    }

    *//**
     * 未获取到的权限回调
     *
     * @param permissions 未获取到的权限列表
     *//*
    protected void onRequestPermissionFailed(String[] permissions) {

    }*/
}