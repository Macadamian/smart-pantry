package com.macadamian.smartpantry.ui.fragments.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.macadamian.smartpantry.R;
import com.macadamian.smartpantry.content.MyContract;
import com.macadamian.smartpantry.database.tables.InventoryTable;
import com.macadamian.smartpantry.ui.activities.ManageLocationsActivity;

public class DialogEditLocation extends DialogFragment {

    private EditText mEdit;

    private final TextWatcher mTextChangedListener = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            ((AlertDialog)getDialog()).getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(
                    s.toString().length() <= ManageLocationsActivity.MAX_NAME_LENGTH &&
                    !s.toString().isEmpty());
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (mEdit.getText().toString().isEmpty()){
             mEdit.setError(getString(R.string.error_no_text));
            }else if (mEdit.getText().length() > ManageLocationsActivity.MAX_NAME_LENGTH){
             mEdit.setError(getString(R.string.error_text_too_long));
            }
        }
    };

    public static DialogEditLocation getInstance(final String inventoryUUID, final String inventoryName) {
        final DialogEditLocation fragment = new DialogEditLocation();
        final Bundle bundle = new Bundle();
        bundle.putString(MyContract.InventoryEntry.COLUMN_INVENTORY_UUID, inventoryUUID);
        bundle.putString(MyContract.InventoryEntry.COLUMN_NAME, inventoryName);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
        final LayoutInflater inflater = LayoutInflater.from(getActivity());

        final View view = inflater.inflate(R.layout.dialog_edit_location, null);
        mEdit = (EditText) view.findViewById(R.id.edit_location_input);
        mEdit.setText(getArguments().getString(MyContract.InventoryEntry.COLUMN_NAME));
        mEdit.setSelection(mEdit.getText().length());
        mEdit.addTextChangedListener(mTextChangedListener);

        builder.setView(view);
        builder.setPositiveButton(R.string.dialog_edit_location_positive, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                saveLocation();
            }
        });

        builder.setNegativeButton(R.string.dialog_edit_location_negative, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        return builder.create();
    }



    private void saveLocation() {
        final String locationToSave = mEdit.getText().toString();
        final String inventoryUUID = getArguments().getString(MyContract.InventoryEntry.COLUMN_INVENTORY_UUID);
        getActivity().getContentResolver().update(MyContract.inventoriesByUuidUri(inventoryUUID), InventoryTable.makeInventoryUpdate(inventoryUUID, locationToSave), null, null);
        dismiss();
    }
}
