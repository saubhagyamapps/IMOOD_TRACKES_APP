package app.food.patient_app.util;

import android.app.ProgressDialog;
import android.content.Context;

import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.fitness.Fitness;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import app.food.patient_app.R;
import app.food.patient_app.retrofit.ApiClient;
import app.food.patient_app.retrofit.ApiInterface;
import app.food.patient_app.sessionmanager.SessionManager;

public class Constant {


  //  public static final String BASE_URL = "http://192.168.1.200/patient_app_server/";
   //  public static final String BASE_URL = "http://www.charmhdwallpapers.com/patient_app/";
     public static final String BASE_URL = "http://51.159.3.125/patient_app/";


    public static ApiInterface apiService =
            ApiClient.getClient().create(ApiInterface.class);

    public static ProgressDialog progressBar;
    public static SessionManager session;
    public static String mUserId;
    public static String mUserName;
    public static String mUserMobile;
    public static String mUserFirebaseID;
    public static String mUserEmail;
    public static String mAddress;
    public static String mImages;
    public static String mGender;
    public static String mPassword;
    public static String mDeviceId;
    public static String mHomeLat;
    public static String mHomeLong;
    public static String mLockCounter;

    public static void progressDialog(Context applicationContext) {
        progressBar = new ProgressDialog(applicationContext);
        progressBar.setCancelable(false);
        progressBar.setTitle(applicationContext.getResources().getString(R.string.progress_dialog_title));
        progressBar.setMessage(applicationContext.getResources().getString(R.string.progress_dialog_msg));
        progressBar.show();

    }


    public static String currentDate() {
        Date todayDate;
        String mCurrentDate;
        todayDate = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        mCurrentDate = df.format(todayDate);
        return mCurrentDate;
    }

    public static void setSession(Context applicationContext) {
        session = new SessionManager(applicationContext);
        HashMap<String, String> user = session.getUserDetails();
        mUserId = user.get(session.KEY_ID);
        mUserName = user.get(session.KEY_NAME);
        mUserMobile = user.get(session.KEY_MOBILE);
        mUserFirebaseID = user.get(session.KEY_FIREBASEID);
        mUserEmail = user.get(session.KEY_EMAIL);
        /*mAddress = user.get(SessionManager.KEY_ADDRESS);
        mImages = user.get(SessionManager.KEY_IMAGES);
        mGender = user.get(SessionManager.KEY_GENDER);*/
        mPassword = user.get(session.KEY_PASSWORD);
        mDeviceId = user.get(session.KEY_DEVICEID);
    }
}
