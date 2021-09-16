package com.saneforce.dms.utils;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.saneforce.dms.model.PrimaryProduct;

import java.util.List;

public class PrimaryProductViewModel extends AndroidViewModel {

    private PrimaryProductRepository repository;
    private LiveData<List<PrimaryProduct>> allData;
    private LiveData<List<PrimaryProduct>> filterData;

    public PrimaryProductViewModel(@NonNull Application application) {
        super(application);
        repository = new PrimaryProductRepository(application);
        allData = repository.getAllConData();
        filterData = repository.getFilterData();
    }

    public void insert(PrimaryProduct contact) {
        repository.insert(contact);
    }

    public void delete(List<PrimaryProduct> contact) {
        repository.delete(contact);
    }

    public LiveData<List<PrimaryProduct>> getAllData() {
        return allData;
    }

    public LiveData<List<PrimaryProduct>> getFilterDatas() {
        return filterData;
    }
}
