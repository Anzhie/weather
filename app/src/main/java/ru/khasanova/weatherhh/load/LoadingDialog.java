package ru.khasanova.weatherhh.load;

import android.app.Dialog;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.View;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.concurrent.atomic.AtomicBoolean;

import ru.khasanova.weatherhh.R;

/**
 * Created by Анжелика.
 */

public class LoadingDialog extends DialogFragment {
    private static final Handler HANDLER = new Handler(Looper.getMainLooper());


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_TITLE, getTheme());
        setCancelable(false);
    }

    @NonNull
    public static LoadingView view(@NonNull FragmentManager fragmentManager){
        return new LoadingDialogView(fragmentManager);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        //создаем диалог загрузки
        return new AlertDialog.Builder(getActivity()).setView(View.inflate(getActivity(), R.layout.dialog_loading, null)).create();
    }


    private static class LoadingDialogView implements LoadingView{
        private final FragmentManager fm;

        private final AtomicBoolean waitForHide;

        private LoadingDialogView (@NonNull FragmentManager fragmentManager){
            //заполняем FragmentManager
            fm = fragmentManager;

            //выясняем, отображен ли диалог загрузки
            boolean shown = fm.findFragmentByTag(LoadingDialog.class.getName()) != null;
            waitForHide = new AtomicBoolean(shown);
        }

        @Override
        public void showLoadingDialog(){
            //если диалог загрузки не отображен, создаем и отображаем его,
            //меняем значение параметра waitForHide(теперь диалог будет отображен)
            if (waitForHide.compareAndSet(false, true)){
                if (fm.findFragmentByTag(LoadingDialog.class.getName()) == null){
                    DialogFragment dialog = new LoadingDialog();
                    dialog.show(fm, LoadingDialog.class.getName());
                }
            }
        }

        @Override
        public void hideLoadingDialog(){
            //если диалог загрузки отображен, выполняем задачу по его скрытию
            //меняем значение параметра waitForHide(теперь диалог НЕ будет отображен)
            if (waitForHide.compareAndSet(true, false)){
                HANDLER.post(new HideLoadingDialogTask(fm));
            }
        }
    }


    private static class HideLoadingDialogTask implements Runnable{
        private final Reference<FragmentManager> fmRef;

        private int attempts = 10;

        public HideLoadingDialogTask(@NonNull FragmentManager fragmentManager) {
            //создаем мягкую ссылку на фрагмент с диалогом загрузки (быстрее очиститься GC)
            fmRef = new WeakReference<>(fragmentManager);
        }

        @Override
        public void run(){
            //очищаем очередь задач
            HANDLER.removeCallbacks(this);

            //получим фрагмент с диалогом загрузки
            final FragmentManager fm = fmRef.get();
            if (fm != null){
                //если он отображен, получим диалог загрузки
                final LoadingDialog dialog = (LoadingDialog) fm.findFragmentByTag(LoadingDialog.class.getName());
                if (dialog != null){
                    //если диалог загрузки на экране, закрываем его
                    dialog.dismissAllowingStateLoss();
                }
                //иначе: если попыток больше 0
                else if (--attempts >=0){
                    //добавляем объект в очередь, объект запустится после задержки 300мс
                    HANDLER.postDelayed(this, 300);
                }
            }
        }
    }
}
