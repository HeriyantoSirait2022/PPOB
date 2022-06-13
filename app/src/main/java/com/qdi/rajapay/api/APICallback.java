package com.qdi.rajapay.api;

import com.android.volley.VolleyError;

import java.util.List;

public class APICallback {

    public interface BaseAPICallback {

        void onAPIResponseFailure(VolleyError error);
    }

    public interface ActionCallback extends BaseAPICallback {

        void onActionResponseSuccess(String message);
    }

    public interface ItemCallback<T> extends BaseAPICallback {

        void onItemResponseSuccess(T item, String message);
    }

    public interface ItemListCallback<T> extends BaseAPICallback {

        void onListResponseSuccess(List<T> list, String message);
    }

    public interface ItemMultipleListCallback<T1, T2> extends BaseAPICallback {

        void onMulitpleListResponseSuccess(List<T1> firstList, List<T2> secondList, String message);
    }

    public interface PagingCallback<T> extends BaseAPICallback {

        void onListResponseSuccess(List<T> list, boolean isLastPage, String message);
    }
}
