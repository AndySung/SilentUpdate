# SilentUpdate
静默更新,socket运用 服务端与客户端

# .jks是系统签名文件，这个需要根据自己的系统来进行签名，需要修改

[SC60][Android7.1][Document]各版本静默安装应用
# Android 不同实现静默安装实现方法
# Android7
/** * *
 * @param context
 * @param filePath
 */
public void installSilentWithReflection(String filePath, String packName) {
    try {
        PackageManager packageManager = mContext.getPackageManager();
        Method method = packageManager.getClass().getDeclaredMethod("installPackage",
        new Class[]{Uri.class, IPackageInstallObserver.class, int.class, String.class}); method.setAccessible(true);
        File apkFile = new File(filePath);
        Uri apkUri = Uri.fromFile(apkFile);
        method.invoke(packageManager, new Object[]{apkUri, new PackInstallObserver(), Integer.valueOf(2), packName});
    } catch (IllegalAccessException e) { e.printStackTrace();
    } catch (Exception e) { e.printStackTrace();
    } 
   }


/** *
*/
static class PackInstallObserver extends IPackageInstallObserver.Stub {
    @Override
    public void packageInstalled(String packageName, int returnCode) throws RemoteException {
        //returnCode
    }
}


# Android 9
 /**
 * @param filepathApk
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public void installPackage(String filepathApk) {
    File file=new File(filepathApk);
    String apkName=filepathApk.substring(filepathApk.lastIndexOf(File.separator)+1,filepathApk.lastIndexOf(". apk"));
    //PackageInstaller
    PackageInstaller packageInstaller = mContext.getPackageManager()
    .getPackageInstaller(); PackageInstaller.SessionParams params=new PackageInstaller
    .SessionParams(PackageInstaller.SessionParams.MODE_FULL_INSTALL); PackageInstaller.Session session=null;
    OutputStream outputStream=null;
    FileInputStream inputStream=null;
    try {
        //Session
      int sessionId = packageInstaller.createSession(params); //Session session=packageInstaller.openSession(sessionId);                                                                  //apksession
      outputStream = session.openWrite(apkName, 0, -1); inputStream=new FileInputStream(file);
      byte[] buffer=new byte[4096];
      int n;
    //apksession
    while ((n=inputStream.read(buffer))>0){
    outputStream.write(buffer,0,n); }
//“files still open”
inputStream.close();
inputStream=null;
outputStream.flush();
outputStream.close();
outputStream=null;
//intentactivity
Intent intent=new Intent();
PendingIntent pendingIntent=PendingIntent.getActivity(mContext,0,intent,0); IntentSender intentSender = pendingIntent.getIntentSender();
// session.commit(intentSender);
    } catch (IOException e) {
        throw new RuntimeException("Couldn't install package", e);
} catch (RuntimeException e) {

if (session != null) { session.abandon();
}
        throw e;
    }finally {
} }
//
packageInstaller.registerSessionCallback(new PackageInstaller.SessionCallback() {
        @Override
        public void onCreated(int sessionId) {
}
        @Override
        public void onBadgingChanged(int sessionId) {
}
        @Override
        public void onActiveChanged(int sessionId, boolean active) {
}
        @Override
        public void onProgressChanged(int sessionId, float progress) {
}
        @Override
        public void onFinished(int sessionId, boolean success) {
}
静默安装需要获取系统权限android.permission.INSTALL_PACKAGES，并且AndroidManifest.xml配置 android:sharedUserId="android.uid.system"，使用 系统签名打包
