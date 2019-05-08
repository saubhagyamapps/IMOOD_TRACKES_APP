package app.food.patient_app.fragment;

import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.budiyev.android.circularprogressbar.CircularProgressBar;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.request.DataSourcesRequest;
import com.google.android.gms.fitness.request.OnDataPointListener;
import com.google.android.gms.fitness.request.SensorRequest;
import com.google.android.gms.fitness.result.DataSourcesResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.concurrent.TimeUnit;

import app.food.patient_app.R;
import app.food.patient_app.model.InsertStepCountModel;
import app.food.patient_app.model.PercentageModel;
import app.food.patient_app.model.StepCountPerModel;
import app.food.patient_app.util.Constant;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class DashBoardFragment extends Fragment implements OnDataPointListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    View mView;
    CircularProgressBar progressBarCall, progressBarSocial, progressBarWalking, progressBarWorking, progressBarStepCount;
    TextView txtCurrentDate, txtStepCount;
    private static final String TAG = "DashBoardFragment";
    private GoogleApiClient mClient = null;
    private OnDataPointListener mListener;
    Handler handler;
    private static final int REQUEST_OAUTH_REQUEST_CODE = 0x1001;
    TextView txtWalkingTime, txtSocialTime, txtWorkingTime, txtCallTime;
    private static final int REQUEST_OAUTH = 1;
    private static final String AUTH_PENDING = "auth_state_pending";
    private boolean authInProgress = false;
    private GoogleApiClient mApiClient;
    String mTotalStep;
    Runnable runnable;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.new_fragment_dash_board, container, false);
        getActivity().setTitle("");
        Constant.setSession(getActivity());
        if (savedInstanceState != null) {
            authInProgress = savedInstanceState.getBoolean(AUTH_PENDING);
        }

        mApiClient = new GoogleApiClient.Builder(getActivity())
                .addApi(Fitness.SENSORS_API)
                .addScope(new Scope(Scopes.FITNESS_ACTIVITY_READ_WRITE))
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        Initialize();
        return mView;
    }

    @Override
    public void onStart() {
        super.onStart();
        mApiClient.connect();
    }

    private void UsesAPICALL() {
        Call<PercentageModel> modelCall = Constant.apiService.getPercentage(Constant.mUserId, Constant.currentDate());
        modelCall.enqueue(new Callback<PercentageModel>() {
            @Override
            public void onResponse(Call<PercentageModel> call, Response<PercentageModel> response) {
                SetProgressBars(response.body().getSocialmediatime(), response.body().getWorktime(), response.body().getCallduration());
                txtSocialTime.setText(String.valueOf(response.body().getSocialmediatime()) + "%");
                txtWorkingTime.setText(String.valueOf(response.body().getWorktime()) + "%");
                txtCallTime.setText(String.valueOf(response.body().getCallduration()) + "%");
                Log.e(TAG, "onResponse:+UsesAPICALL ");
            }

            @Override
            public void onFailure(Call<PercentageModel> call, Throwable t) {
                Log.e(TAG, "onFailure:+UsesAPICALL " + t.getMessage());
            }
        });
    }

    public void countStepCall() {
        FitnessOptions fitnessOptions =
                FitnessOptions.builder()
                        .addDataType(DataType.TYPE_STEP_COUNT_CUMULATIVE)
                        .addDataType(DataType.TYPE_STEP_COUNT_DELTA)
                        .build();
        if (!GoogleSignIn.hasPermissions(GoogleSignIn.getLastSignedInAccount(getActivity()), fitnessOptions)) {
            GoogleSignIn.requestPermissions(
                    this,
                    REQUEST_OAUTH_REQUEST_CODE,
                    GoogleSignIn.getLastSignedInAccount(getActivity()),
                    fitnessOptions);
        } else {


            handler = new Handler();

            runnable = new Runnable() {
                public void run() {
                    subscribe();
                    handler.postDelayed(this, 1000);
                }
            };
            handler.postDelayed(runnable, 1000);
        }
    }


    public void subscribe() {
        // To create a subscription, invoke the Recording API. As soon as the subscription is
        // active, fitness data will start recording.
        try {
            Fitness.getRecordingClient(getActivity(), GoogleSignIn.getLastSignedInAccount(getActivity()))
                    .subscribe(DataType.TYPE_STEP_COUNT_CUMULATIVE)
                    .addOnCompleteListener(
                            new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.i(TAG, "Successfully subscribed!");

                                        readData();
                                    } else {
                                        Log.w(TAG, "There was a problem subscribing.", task.getException());
                                    }
                                }
                            });
        } catch (Exception e) {

        }

    }

    /**
     * Reads the current daily step total, computed from midnight of the current day on the device's
     * current timezone.
     */
    private void readData() {
        try {
            Log.e(TAG, "readData:fsfdsfsfs ");
            Fitness.getHistoryClient(getActivity(), GoogleSignIn.getLastSignedInAccount(getActivity()))
                    .readDailyTotal(DataType.TYPE_STEP_COUNT_DELTA)
                    .addOnSuccessListener(
                            new OnSuccessListener<DataSet>() {
                                @Override
                                public void onSuccess(DataSet dataSet) {
                                    final long total =
                                            dataSet.isEmpty()
                                                    ? 0
                                                    : dataSet.getDataPoints().get(0).getValue(Field.FIELD_STEPS).asInt();
                                    Log.i(TAG, "Total steps: " + total);
                                    txtStepCount.setText(total + " Steps");
//                                    Toast.makeText(getActivity(), "Total Step " + total, Toast.LENGTH_SHORT).show();
                                    mTotalStep = String.valueOf(total);

                                }
                            })
                    .addOnFailureListener(
                            new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w(TAG, "There was a problem getting the step count.", e);
                                }
                            });
        } catch (Exception e) {

        }
    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable);
    }

    private void Initialize() {

        progressBarCall = mView.findViewById(R.id.progressBarCall);
        progressBarSocial = mView.findViewById(R.id.progressBarSocial);
        //  progressBarWalking = mView.findViewById(R.id.progressBarWalking);
        progressBarWorking = mView.findViewById(R.id.progressBarWorking);
        progressBarStepCount = mView.findViewById(R.id.progressBarStepCount);
        txtCurrentDate = mView.findViewById(R.id.txtCurrentDate);
        txtStepCount = mView.findViewById(R.id.txtStepCount);
        txtCurrentDate.setText("Today");
        // txtWalkingTime = mView.findViewById(R.id.txtWalkingTime);
        txtSocialTime = mView.findViewById(R.id.txtSocialTime);
        txtWorkingTime = mView.findViewById(R.id.txtWorkingTime);
        txtCallTime = mView.findViewById(R.id.txtCallTime);
        UsesAPICALL();
        countStepCall();
        StepCountAPICALL();
    }

    private void StepCountAPICALL() {
        Call<StepCountPerModel> count = Constant.apiService.getStepPer(Constant.mUserId, Constant.currentDate());
        count.enqueue(new Callback<StepCountPerModel>() {
            @Override
            public void onResponse(Call<StepCountPerModel> call, Response<StepCountPerModel> response) {
                if (response.body().getStatus() == 0) {
                    progressBarStepCount.setProgress(response.body().getPercentage());
                }

            }

            @Override
            public void onFailure(Call<StepCountPerModel> call, Throwable t) {

            }
        });
    }

    public void SetProgressBars(long Social, long worktime, long callTime) {
        progressBarCall.setProgress(callTime);

        progressBarWorking.setProgress(worktime);

        progressBarSocial.setProgress(Social);
        Log.e(TAG, "SetProgressBars: ");

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onConnected(Bundle bundle) {
        DataSourcesRequest dataSourceRequest = new DataSourcesRequest.Builder()
                .setDataTypes(DataType.TYPE_STEP_COUNT_CUMULATIVE)
                .setDataSourceTypes(DataSource.TYPE_RAW)
                .build();

        ResultCallback<DataSourcesResult> dataSourcesResultCallback = new ResultCallback<DataSourcesResult>() {
            @Override
            public void onResult(DataSourcesResult dataSourcesResult) {
                for (DataSource dataSource : dataSourcesResult.getDataSources()) {
                    if (DataType.TYPE_STEP_COUNT_CUMULATIVE.equals(dataSource.getDataType())) {
                        registerFitnessDataListener(dataSource, DataType.TYPE_STEP_COUNT_CUMULATIVE);
                    }
                }
            }
        };

        Fitness.SensorsApi.findDataSources(mApiClient, dataSourceRequest)
                .setResultCallback(dataSourcesResultCallback);
    }

    private void registerFitnessDataListener(DataSource dataSource, DataType dataType) {

        Log.e(TAG, "registerFitnessDataListener: " + dataType);
        SensorRequest request = new SensorRequest.Builder()
                .setDataSource(dataSource)
                .setDataType(dataType)
                .setSamplingRate(1, TimeUnit.SECONDS)
                .build();

        Fitness.SensorsApi.add(mApiClient, request, this)
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        if (status.isSuccess()) {
                            Log.e("GoogleFit", "SensorApi successfully added");
                        }
                    }
                });

    }


    @Override
    public void onConnectionSuspended(int i) {

    }


    @Override
    public void onDataPoint(DataPoint dataPoint) {
        Log.e(TAG, "onDataPoint:--->>>--->> " + dataPoint);
       /* for (final Field field : dataPoint.getDataType().getFields()) {
            final Value value = dataPoint.getValue(field);
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
              //      Toast.makeText(getActivity(), "Field: " + field.getName() + " Value: " + value, Toast.LENGTH_SHORT).show();
                    //txtStepCount.setText(value + " Steps");

                }
            });
        }*/
        handler = new Handler();

        final Runnable r = new Runnable() {
            public void run() {
                insertStepApiCall(mTotalStep);
                handler.postDelayed(this, 4000);
            }
        };
        handler.postDelayed(r, 4000);
    }

    private void insertStepApiCall(String value) {
        Call<InsertStepCountModel> modelCall = Constant.apiService.insertStep(Constant.mUserId, Constant.currentDate(), value);
        modelCall.enqueue(new Callback<InsertStepCountModel>() {
            @Override
            public void onResponse(Call<InsertStepCountModel> call, Response<InsertStepCountModel> response) {

            }

            @Override
            public void onFailure(Call<InsertStepCountModel> call, Throwable t) {

            }
        });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if (!authInProgress) {
            try {
                authInProgress = true;
                connectionResult.startResolutionForResult(getActivity(), REQUEST_OAUTH);
            } catch (IntentSender.SendIntentException e) {

            }
        } else {
            Log.e("GoogleFit", "authInProgress");
        }

    }

    @Override
    public void onStop() {
        super.onStop();

        Fitness.SensorsApi.remove(mApiClient, this)
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        if (status.isSuccess()) {
                            mApiClient.disconnect();
                        }
                    }
                });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(AUTH_PENDING, authInProgress);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_OAUTH) {
            authInProgress = false;
            if (resultCode == RESULT_OK) {
                if (!mApiClient.isConnecting() && !mApiClient.isConnected()) {
                    mApiClient.connect();
                }
            } else if (resultCode == RESULT_CANCELED) {
                Log.e("GoogleFit", "RESULT_CANCELED");
            }
        } else {
            Log.e("GoogleFit", "requestCode NOT request_oauth");
        }
    }
}
