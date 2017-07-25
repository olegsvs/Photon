package ru.dmisb.photon.screens.new_card;

import android.databinding.BindingAdapter;
import android.widget.ImageView;

import ru.dmisb.photon.R;

public class NewCardBindingAdapter {

    @BindingAdapter("selected")
    public static void bindSelectedBorder(ImageView image, NewCardAlbum viewModel) {
        if (viewModel != null && viewModel.isSelected()) {
            image.setBackground(image.getResources().getDrawable(R.drawable.album_gradient_border));
        } else {
            image.setBackground(image.getResources().getDrawable(R.drawable.album_gradient));
        }
    }
}
