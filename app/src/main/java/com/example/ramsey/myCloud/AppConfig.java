package com.example.ramsey.myCloud;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by young on 2018/3/16.
 */

public class AppConfig {
    // Server user login url


    public static String URL_LOGIN = "http://192.168.1.108/myCloud/Login.php";

    // Server user register url
    public static String URL_REGISTER = "http://192.168.1.108/myCloud/Register.php";

    // Server user resetPwd url
    public static String URL_RESET = "http://192.168.1.108/myCloud/ResetPwd.php";

    // File upload url (replace the ip with your server address)
    public static final String FILE_UPLOAD_URL = "http://192.168.1.108/myCloud/FileUpload.php";

    // Directory name to store captured images and videos
    public static final String IMAGE_DIRECTORY_NAME = "Android File Upload";

    // Server create url
    public static final String URL_CREATE = "http://192.168.1.108/myCloud/CreateProblemList.php";
    
    //Server Check ProblemList url
    public static final String URL_CheckProblemList = "http://192.168.1.108/myCloud/CheckProblemList.php";
    
    //Server Upload Reason url
    public static final String URL_ReportCause="http://192.168.1.108/myCloud/ReportCause.php";
    
    //Server Upload Action url
    public static  final String URL_ReportAction="http://192.168.1.108/myCloud/ReportSolution.php";
    
    //Server Check CauseList url
    public static final String URL_CheckCauseList="http://192.168.1.108/myCloud/CheckCause.php";
    
    //Server Check ActionList url
    public static final String URL_CheckSolutionList="http://192.168.1.108/myCloud/CheckSolution.php";
    
    //Server Get Cause Detail url
    public static final String URL_CauseDetail="http://192.168.1.108/myCloud/CauseDetail.php";
    
    //Server Get Solution Detail url
    public static final String URL_SolutionDetail="http://192.168.1.108/myCloud/SolutionDetail.php";
    
    //Server Edit Cause Detail url
    public static final String URL_EditCause="http://192.168.1.108/myCloud/EditCause.php";
    
    //Server Edit Action Detail url
    public static final String URL_EditAction="http://192.168.1.108/myCloud/EditSolution.php";
    
    //Server Delete Cause url
    public static final String URL_DeleteCause="http://192.168.1.108/myCloud/DeleteCause.php";
    
    //Server Delete Action url
    public static final String URL_DeleteAction="http://192.168.1.108/myCloud/DeleteSolution.php";

    // Problem list detail create url
    public static String URL_ProblemDetail = "http://192.168.1.108/myCloud/ProblemListDetail.php";

    // Problem list detail create url
    public static String URL_EditProblemList = "http://192.168.1.108/myCloud/EditProblemList.php";

    // Problem list detail create url
    public static String URL_SelfCheck = "http://192.168.1.108/myCloud/SelfCheck.php";

    // Problem list detail create url
    public static String URL_Feedback_Imgae = "http://192.168.1.108/myCloud/FeedbackImage.php";

    // Problem list detail create url
    public static String URL_Report_Feedback = "http://192.168.1.108/myCloud/ReportFeedback.php";

    // Problem list detail create url
    public static String URL_SelfCheck_Problem= "http://192.168.1.108/myCloud/SelfCheckProblem.php";

    // Problem list detail create url
    public static String URL_Query_Problem= "http://192.168.1.108/myCloud/SearchForProblemList.php";
  
   // Check Theoretic State URL
    public static String URL_CheckTheoreticState = "http://192.168.1.108/myCloud/CheckTheoreticState.php";

    // Edit Theoretic State URL
    public static String URL_EditTheoreticState = "http://192.168.1.108/myCloud/EditTheoreticState.php";

    //Upload Theoretic State Image
    public static String URL_TheoreticStateImage="http://192.168.1.108/myCloud/TheoreticStateImage.php";

    // Edit Improve State URL
    public static String URL_EditImproveState = "http://192.168.1.108/myCloud/EditImproveState.php";

    // Check Improve State URL
    public static String URL_CheckImproveState = "http://192.168.1.108/myCloud/CheckImproveState.php";

    //Upload Theoretic State Image
    public static String URL_ImproveStateImage="http://192.168.1.108/myCloud/ImproveStateImage.php";

    //Check Temp Solutions
    public static String URL_CheckTempSolutionList="http://192.168.1.108/myCloud/CheckTempSolution.php";

    //Get Temp Solutions Details
    public static String URL_TempSolutionDetail="http://192.168.1.108/myCloud/TempSolutionDetail.php";

    //Report Temp Solution
    public static String URL_ReportTempAction="http://192.168.1.108/myCloud/ReportTempSolution.php";

    //Upload TempSolution Image
    public static String URL_TempSolutionImage="http://192.168.1.108/myCloud/TempSolutionImage.php";

    //Edit Temp Solution
    public static String URL_EditTempSolution="http://192.168.1.108/myCloud/EditTempSolution.php";

    //Upload TempSolution Image
    public static String URL_DeleteTempSolution="http://192.168.1.108/myCloud/DeleteTempSolution.php";

    //Upload TempSolution Image
    public static String URL_PIECHART="http://192.168.1.108/myCloud/PieChart.php";

    //Upload TempSolution Image
    public static String URL_LINECHART="http://192.168.1.108/myCloud/LineChart.php";

    //Upload TempSolution Image
    public static String URL_Export="http://192.168.1.108/myCloud/Export.php";




    /**
     * Turn drawable resource into byte array.
     *
     * @param context parent context
     * @param id      drawable resource id
     * @return byte array
     */
    public static byte[] getFileDataFromDrawable(Context context, int id) {
        Drawable drawable = ContextCompat.getDrawable(context, id);
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    /**
     * Turn drawable into byte array.
     *
     * @param drawable data
     * @return byte array
     */
    public static byte[] getFileDataFromDrawable(Context context, Drawable drawable) {
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    public static String getFileDataFromUri(Context context, Uri uri) {
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);
        byte[] a = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(a, Base64.DEFAULT);
    }

}

//192.168.1.108 192.168.1.108
