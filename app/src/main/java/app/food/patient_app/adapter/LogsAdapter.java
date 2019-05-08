package app.food.patient_app.adapter;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.RequiresPermission;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.wickerlabs.logmanager.LogObject;
import com.wickerlabs.logmanager.LogsManager;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

import app.food.patient_app.R;

public class LogsAdapter extends ArrayAdapter<LogObject> {

    List<LogObject> logs;
    Context context;
    int resource;

    public LogsAdapter(Context context, int resource, List<LogObject> callLogs) {
        super(context, resource, callLogs);
        this.logs = callLogs;
        this.context = context;
        this.resource = resource;

    }

    @Override
    public Context getContext() {
        return context;
    }

    @Override
    public int getCount() {
        return logs.size();
    }

    @Override
    public LogObject getItem(int position) {
        return logs.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = LayoutInflater.from(getContext()).inflate(resource, parent, false);

        TextView phone =  row.findViewById(R.id.phoneNum);
        TextView duration =  row.findViewById(R.id.callDuration);
        TextView date =  row.findViewById(R.id.callDate);
        ImageView imageView =  row.findViewById(R.id.callImage);

        LogObject log = getItem(position);
        Date date1 = new Date(log.getDate());

        DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.ERA_FIELD, DateFormat.SHORT);
        phone.setText(log.getContactName());
        duration.setText(log.getCoolDuration());
        date.setText(dateFormat.format(date1));

        switch (log.getType()) {

            case LogsManager.INCOMING:
                imageView.setImageResource(R.drawable.received);
                break;
            case LogsManager.OUTGOING:
                imageView.setImageResource(R.drawable.sent);
                break;
            case LogsManager.MISSED:
                imageView.setImageResource(R.drawable.missed);
                break;
            default:
                imageView.setImageResource(R.drawable.cancelled);
                break;

        }


        return row;
    }
}
