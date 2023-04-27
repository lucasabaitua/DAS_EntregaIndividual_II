package com.example.myapplication;
import android.content.Context;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.TimeUnit;

public class conexionBBDDRemota extends Worker{
    public conexionBBDDRemota(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    Constraints restricciones = new Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build();
    PeriodicWorkRequest trabajoRepetitivo = new PeriodicWorkRequest.Builder(conexionBBDDRemota.class,25, TimeUnit.MINUTES)
            .setConstraints(restricciones)
            .build();
       // WorkManager.getInstance(this).enqueue(trabajoRepetitivo);
    @NonNull
    @Override
    public Result doWork() {

        return Result.success();
    }
    String direccion = "http://servidor/servicioweb.php";
    /*
    String workInfo;
    OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(ConexionServidorDAS.class).build();
    WorkManager.getInstance(this).getWorkInfoByIdLiveData(otwr.getId())
            .observe(this, new Observer<WorkInfo>() {
        @Override
        public void onChanged(WorkInfo workInfo) {
            if(workInfo != null && workInfo.getState().isFinished()){
                TextView textViewResult = findViewById(R.id.textoResultado);
                textViewResult.setText(workInfo.getOutputData().getString("datos"));
            }
        }
    });
WorkManager.getInstance(this).enqueue(otwr);
*/



}
