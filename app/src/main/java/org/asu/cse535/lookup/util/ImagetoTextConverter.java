package org.asu.cse535.lookup.util;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.VisionRequestInitializer;
import com.google.api.services.vision.v1.model.AnnotateImageRequest;
//import com.google.api.services.vision.v1.model.AnnotateImageResponse;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesResponse;
import com.google.api.services.vision.v1.model.EntityAnnotation;
import com.google.api.services.vision.v1.model.Feature;
import com.google.api.services.vision.v1.model.Image;
import com.google.api.services.vision.v1.model.ImageContext;
import com.google.api.services.vision.v1.model.WebEntity;
//import com.google.cloud.vision.v1.AnnotateImageResponse;
//import com.google.cloud.vision.v1.LocalizedObjectAnnotation;

import org.asu.cse535.lookup.activity.MainActivity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static android.content.ContentValues.TAG;

public class ImagetoTextConverter {



    public static List<String> labelList = null;
    public static MainActivity mainActivity = null;
    public static boolean compress = false;

    public static List<String> getTextListFromImage(String path, VisionRequestInitializer requestInitializer, MainActivity activity, boolean compressMain) {

        labelList = null;
        mainActivity = activity;
        compress = compressMain;
        performCloudVisionRequest(path, requestInitializer);
        return labelList;
    }

    public static void performCloudVisionRequest(String path, VisionRequestInitializer requestInitializer) {
        if (path != "") {
            try {
                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                Bitmap bitmap = resizeBitmap(BitmapFactory.decodeFile(path, bmOptions), 1024);
                if (compress) bitmap = resizeBitmap(BitmapFactory.decodeFile(path, bmOptions), 512);
                callCloudVision(bitmap, requestInitializer);
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private static void callCloudVision(final Bitmap bitmap, final VisionRequestInitializer requestInitializer) throws IOException {

        new AsyncTask<Object, Void, BatchAnnotateImagesResponse>() {
            @Override
            protected BatchAnnotateImagesResponse doInBackground(Object... params) {
                try {

                    HttpTransport httpTransport = AndroidHttp.newCompatibleTransport();
                    JsonFactory jsonFactory = GsonFactory.getDefaultInstance();

                    Vision.Builder builder = new Vision.Builder
                            (httpTransport, jsonFactory, null);
                    builder.setVisionRequestInitializer(requestInitializer);
                    Vision vision = builder.build();

                    List<Feature> featureList = new ArrayList<>();
                    Feature labelDetection = new Feature();
                    labelDetection.setType("LABEL_DETECTION");
                    labelDetection.setMaxResults(6);
                    featureList.add(labelDetection);
                    Feature logoDetection = new Feature();
                    logoDetection.setType("LOGO_DETECTION");
                    logoDetection.setMaxResults(6);
                    featureList.add(logoDetection);
                    Feature textDetection = new Feature();
                    textDetection.setType("WEB_DETECTION");
                    textDetection.setMaxResults(6);
                    featureList.add(textDetection);
                    Feature objectLoc = new Feature();
                    objectLoc.setType("DOCUMENT_TEXT_DETECTION");
                    objectLoc.setMaxResults(6);
                    featureList.add(objectLoc);
                    List<ImageContext> objectImageContext = new ArrayList<>();
                    ImageContext productSearch = new ImageContext();


                    List<AnnotateImageRequest> imageList = new ArrayList<>();
                    AnnotateImageRequest annotateImageRequest = new AnnotateImageRequest();
                    Image base64EncodedImage = getBase64EncodedJpeg(bitmap);
                    annotateImageRequest.setImage(base64EncodedImage);
                    annotateImageRequest.setFeatures(featureList);
                   // annotateImageRequest.setImageContext()
                    imageList.add(annotateImageRequest);

                    BatchAnnotateImagesRequest batchAnnotateImagesRequest =
                            new BatchAnnotateImagesRequest();
                    batchAnnotateImagesRequest.setRequests(imageList);

                    Vision.Images.Annotate annotateRequest =
                            vision.images().annotate(batchAnnotateImagesRequest);
                    annotateRequest.setDisableGZipContent(true);
                    Log.d(TAG, "Sending request to Google Cloud");

                    BatchAnnotateImagesResponse response = annotateRequest.execute();
                    return response;

                } catch (GoogleJsonResponseException e) {
                    Log.e(TAG, "Request error: " + e.getContent());
                } catch (IOException e) {
                    Log.d(TAG, "Request error: " + e.getMessage());
                }
                return null;
            }

            protected void onPostExecute(BatchAnnotateImagesResponse response) {
                labelList = getDetectedTexts(response);
                System.out.println("/n"+labelList);
                labelList.remove(null);
                mainActivity.progressDialog.dismiss();
                mainActivity.navigate(labelList);
            }

        }.execute();
    }

    private static ArrayList<String> getDetectedTexts(BatchAnnotateImagesResponse response) {
        ArrayList<String> message = new ArrayList();
        List<EntityAnnotation> labels = response.getResponses().get(0).getLabelAnnotations();
        List<EntityAnnotation> res = response.getResponses().get(0).getLogoAnnotations();
        List<EntityAnnotation> res1 = response.getResponses().get(0).getTextAnnotations();
       // List<ColorInfo> res1 = response.getResponses().get(0).getImagePropertiesAnnotation().getDominantColors().getColors();
        List<WebEntity> res2 = response.getResponses().get(0).getWebDetection().getWebEntities();
        if (res2 != null) {
            for (WebEntity text : res2) {
                if(text.getDescription()!=null&&text.getDescription().length()<10){
                message.add(text.getDescription());
            }}
        }
        if (res1 != null) {
            for (EntityAnnotation text : res1) {
                if(text.getDescription()!=null&&text.getDescription().length()<10){
                message.add(text.getDescription());
            }
        }}
        if (labels != null) {
            for (EntityAnnotation text : labels) {
                if(text.getDescription()!=null&&text.getDescription().length()<10){
                message.add(text.getDescription());
            }}
        }
        Set<String> m = new HashSet<>();
        m.addAll(message);
        message.clear();
        message.addAll(m);
        return message;
    }

    public static Bitmap resizeBitmap(Bitmap bitmap, int dimension) {

        int maxDimension = dimension;
        int originalWidth = bitmap.getWidth();
        int originalHeight = bitmap.getHeight();
        int resizedWidth = maxDimension;
        int resizedHeight = maxDimension;

        if (originalHeight > originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = (int) (resizedHeight * (float) originalWidth / (float) originalHeight);
        } else if (originalWidth > originalHeight) {
            resizedWidth = maxDimension;
            resizedHeight = (int) (resizedWidth * (float) originalHeight / (float) originalWidth);
        } else if (originalHeight == originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = maxDimension;
        }
        return Bitmap.createScaledBitmap(bitmap, resizedWidth, resizedHeight, false);
    }

    public static Image getBase64EncodedJpeg(Bitmap bitmap) {
        Image image = new Image();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
        byte[] imageBytes = byteArrayOutputStream.toByteArray();
        image.encodeContent(imageBytes);
        return image;
    }

}
