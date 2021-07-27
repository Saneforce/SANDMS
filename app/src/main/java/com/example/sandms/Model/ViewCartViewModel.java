package com.example.sandms.Model;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.sandms.Utils.PrimaryProductRepository;

import java.util.List;

public class ViewCartViewModel extends AndroidViewModel {

    private PrimaryProductRepository repository;
    private LiveData<List<PrimaryProduct>> allData;
    private LiveData<List<PrimaryProduct>> filterData;

    public ViewCartViewModel(@NonNull Application application) {
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
    } public LiveData<List<PrimaryProduct>> getFilterDatas() {
        return filterData;
    }
}
