package app.food.patient_app.adapter;

import android.content.Context;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.List;

import app.food.patient_app.R;
import app.food.patient_app.model.GetSocialUsageListModel;
import app.food.patient_app.util.AppUtil;

public class AppUsageMonthlyAdaptar extends RecyclerView.Adapter<AppUsageMonthlyAdaptar.MyViewHolder> {

    String result;
    Context mContext;
    List<GetSocialUsageListModel.ResultBean> resultBeans ;

    public AppUsageMonthlyAdaptar(Context mContext, List<GetSocialUsageListModel.ResultBean> resultBeans) {
        this.mContext = mContext;
        this.resultBeans = resultBeans;
    }

    @NonNull
    @Override
    public AppUsageMonthlyAdaptar.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View mView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.app_usage_list,viewGroup,false);
        return new AppUsageMonthlyAdaptar.MyViewHolder(mView);
    }

    @Override
    public void onBindViewHolder(@NonNull AppUsageMonthlyAdaptar.MyViewHolder myViewHolder, int i) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        myViewHolder.txt_App_Name.setText(resultBeans.get(i).getApplication());
        myViewHolder.txt_Usage_Time.setText(AppUtil.formatMilliSeconds(resultBeans.get(i).getTotalTime()));


    }

    @Override
    public int getItemCount() {
        return resultBeans.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView txt_App_Name, txt_Usage_Time;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            txt_App_Name = itemView.findViewById(R.id.txt_app_name);
            txt_Usage_Time = itemView.findViewById(R.id.txt_app_usage_time);
        }
    }
}
