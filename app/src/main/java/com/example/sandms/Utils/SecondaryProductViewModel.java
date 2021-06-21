package com.example.sandms.Utils;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.sandms.Model.PrimaryProduct;
import com.example.sandms.Model.SecondaryProduct;

import java.util.List;

public class SecondaryProductViewModel extends AndroidViewModel {

    private SecProductRepository repository;
    private LiveData<List<SecondaryProduct>> allData;
    private LiveData<List<SecondaryProduct>> filterData;

    public SecondaryProductViewModel(@NonNull Application application) {
        super(application);
        repository = new SecProductRepository(application);
        allData = repository.getAllConData();
        filterData = repository.getFilterData();
    }

    public void insert(SecondaryProduct contact) {
        repository.insert(contact);
    }

    public void delete(List<SecondaryProduct> contact) {
        repository.delete(contact);
    }

    public LiveData<List<SecondaryProduct>> getAllData() {
        return allData;
    } public LiveData<List<SecondaryProduct>> getFilterDatas() {
        return filterData;
    }
}
