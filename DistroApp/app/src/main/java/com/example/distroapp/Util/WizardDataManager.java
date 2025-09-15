package com.example.distroapp.Util;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

/**
 * Gestore centralizzato per i dati del wizard di creazione richiesta
 * Salva automaticamente in SharedPreferences
 */
public class WizardDataManager {

    private static final String PREFS_NAME = "wizard_richiesta";
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    // Keys per SharedPreferences Step 2
    private static final String KEY_STEP2_CPU = "step2_cpu";
    private static final String KEY_STEP2_RAM = "step2_ram";
    private static final String KEY_STEP2_STORAGE_SIZE = "step2_storage_size";
    private static final String KEY_STEP2_STORAGE_TYPE = "step2_storage_type";
    private static final String KEY_STEP2_GPU_TYPE = "step2_gpu_type";
    private static final String KEY_STEP2_GPU_DETAILS = "step2_gpu_details";

    public WizardDataManager(Context context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    // ===== STEP 2: HARDWARE =====
    public void saveStep2Data(String cpu, String ram, String storageSize,
                              String storageType, String gpuType, String gpuDetails) {
        editor.putString(KEY_STEP2_CPU, cpu);
        editor.putString(KEY_STEP2_RAM, ram);
        editor.putString(KEY_STEP2_STORAGE_SIZE, storageSize);
        editor.putString(KEY_STEP2_STORAGE_TYPE, storageType);
        editor.putString(KEY_STEP2_GPU_TYPE, gpuType);
        editor.putString(KEY_STEP2_GPU_DETAILS, gpuDetails);
        editor.apply();
    }

    public Bundle getStep2Data() {
        Bundle bundle = new Bundle();
        bundle.putString("cpu", prefs.getString(KEY_STEP2_CPU, ""));
        bundle.putString("ram", prefs.getString(KEY_STEP2_RAM, ""));
        bundle.putString("storageSize", prefs.getString(KEY_STEP2_STORAGE_SIZE, ""));
        bundle.putString("storageType", prefs.getString(KEY_STEP2_STORAGE_TYPE, ""));
        bundle.putString("gpuType", prefs.getString(KEY_STEP2_GPU_TYPE, ""));
        bundle.putString("gpuDetails", prefs.getString(KEY_STEP2_GPU_DETAILS, ""));
        return bundle;
    }

    public void clearAllData() {
        editor.clear();
        editor.apply();
    }
}
