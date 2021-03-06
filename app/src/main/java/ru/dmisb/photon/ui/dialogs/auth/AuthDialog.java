package ru.dmisb.photon.ui.dialogs.auth;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import java.net.SocketTimeoutException;

import javax.inject.Inject;

import io.reactivex.Observable;
import ru.dmisb.photon.R;
import ru.dmisb.photon.data.repo.Repository;
import ru.dmisb.photon.databinding.DialogAuthBinding;
import ru.dmisb.photon.di.DaggerScope;
import ru.dmisb.photon.di.components.RootComponent;
import ru.dmisb.photon.di.modules.RootModule;
import ru.dmisb.photon.flow.ScreenScoper;

@SuppressWarnings("unused")
public class AuthDialog extends AlertDialog {

    @Inject
    Repository repository;

    private AuthDialog(@NonNull Context context) {
        super(context);
        initComponent();
    }

    private void initComponent() {
        RootComponent rootComponent = ScreenScoper.getComponent(ScreenScoper.ROOT_SCOPE_NAME);
        if (rootComponent != null) {
            Component component = rootComponent.plusAuthComponent();
            if (component != null)
                component.inject(this);
        }
    }

    public static Observable<Boolean> showDialog(Context context) {
        return Observable.create(e -> {

            LayoutInflater inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.dialog_auth, null);
            DialogAuthBinding binding = DataBindingUtil.bind(view);
            AuthViewModel viewModel = new AuthViewModel();
            binding.setModel(viewModel);

            AuthDialog dialog = new AuthDialog(context);
            dialog.setView(view);
            dialog.setCancelable(true);
            dialog.setCanceledOnTouchOutside(true);
            dialog.setTitle(context.getResources().getString(R.string.auth_title));

            binding.authOk.setOnClickListener(v -> {
                if (viewModel.isEmailValid() && viewModel.isPasswordValid()) {
                    dialog.repository
                            .signIn(viewModel.getEmail(), viewModel.getPassword())
                            .subscribe(
                                    aBoolean -> {
                                        dialog.dismiss();
                                        e.onNext(aBoolean);
                                    },
                                    throwable -> {
                                        if (throwable instanceof SocketTimeoutException)
                                            Toast.makeText(context.getApplicationContext(),
                                                    context.getString(R.string.err_server),
                                                    Toast.LENGTH_SHORT
                                            ).show();
                                        else
                                            Toast.makeText(context.getApplicationContext(),
                                                    throwable.getMessage(),
                                                    Toast.LENGTH_SHORT
                                            ).show();
                                    }

                            );
                }
            });

            binding.authCancel.setOnClickListener(v -> {
                dialog.cancel();
                e.onNext(false);
            });

            dialog.show();
        });
    }

    //region ================= DI =================

    @dagger.Subcomponent(modules = RootModule.class)
    @DaggerScope(AuthDialog.class)
    public interface Component {
        void inject(AuthDialog dialog);
    }

    //endregion
}