package com.example.sandms.Utils;

import android.app.Application;
import android.os.AsyncTask;
import androidx.lifecycle.LiveData;
import com.example.sandms.Interface.SecProductDao;
import com.example.sandms.Model.SecondaryProduct;

import java.util.List;

public class SecProductRepository {
    private SecProductDao contactDao;
    private LiveData<List<SecondaryProduct>> allConData;
    private LiveData<List<SecondaryProduct>> filterConData;


    public SecProductRepository(Application application) {
        SecondaryProductDatabase database = SecondaryProductDatabase.getInstance(application);
        contactDao = database.contactDao();
        allConData = contactDao.getAllContact();
        filterConData = contactDao.getFilterData();
    }

    public void insert(SecondaryProduct contact) {
        new InsertContactAsynTask(contactDao).execute(contact);
    }

    public void delete(List<SecondaryProduct> contact) {
        new DeleteContactAsynTask(contactDao).execute(contact);
    }

    public LiveData<List<SecondaryProduct>> getAllConData() {
        return allConData;
    }
    public LiveData<List<SecondaryProduct>> getFilterData() {
        return filterConData;
    }

    public static class InsertContactAsynTask extends AsyncTask<SecondaryProduct, Void, Void> {
        private SecProductDao mContactDao;

        public InsertContactAsynTask(SecProductDao mContactDao) {
            this.mContactDao = mContactDao;
        }

        @Override
        protected Void doInBackground(SecondaryProduct... contacts) {
            mContactDao.insert(contacts[0]);
            return null;
        }
    }


    public static class DeleteContactAsynTask extends AsyncTask<List<SecondaryProduct>, Void, Void> {
        private SecProductDao mContactDao;

        public DeleteContactAsynTask(SecProductDao mContactDao) {
            this.mContactDao = mContactDao;
        }

        @Override
        protected Void doInBackground(List<SecondaryProduct>... contacts) {
            mContactDao.delete(contacts[0]);
            return null;
        }
    }
}
