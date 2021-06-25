package com.example.sandms.Utils;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.example.sandms.Interface.PrimaryProductDao;
import com.example.sandms.Model.PrimaryProduct;

import java.util.List;

public class PrimaryProductRepository {
    private PrimaryProductDao contactDao;
    private LiveData<List<PrimaryProduct>> allConData;
    private LiveData<List<PrimaryProduct>> filterConData;


    public PrimaryProductRepository(Application application) {
        PrimaryProductDatabase database = PrimaryProductDatabase.getInstance(application);
        contactDao = database.contactDao();
        allConData = contactDao.getAllContact();
        filterConData = contactDao.getFilterData();
    }

    public void insert(PrimaryProduct contact) {
        new InsertContactAsynTask(contactDao).execute(contact);
    }

    public void delete(List<PrimaryProduct> contact) {
        new DeleteContactAsynTask(contactDao).execute(contact);
    }

    public LiveData<List<PrimaryProduct>>
    getAllConData() {
        return allConData;
    }
    public LiveData<List<PrimaryProduct>> getFilterData() {
        return filterConData;
    }

    public static class InsertContactAsynTask extends AsyncTask<PrimaryProduct, Void, Void> {
        private PrimaryProductDao mContactDao;

        public InsertContactAsynTask(PrimaryProductDao mContactDao) {
            this.mContactDao = mContactDao;
        }

        @Override
        protected Void doInBackground(PrimaryProduct... contacts) {
            mContactDao.insert(contacts[0]);
            return null;
        }
    }


    public static class DeleteContactAsynTask extends AsyncTask<List<PrimaryProduct>, Void, Void> {
        private PrimaryProductDao mContactDao;

        public DeleteContactAsynTask(PrimaryProductDao mContactDao) {
            this.mContactDao = mContactDao;
        }

        @Override
        protected Void doInBackground(List<PrimaryProduct>... contacts) {
            mContactDao.delete(contacts[0]);
            return null;
        }
    }
}
