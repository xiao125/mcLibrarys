package com.mc.game;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;

public class ViewDialogFragment extends DialogFragment {

	
	
	public void show(FragmentManager fragmentManager) {
        show(fragmentManager, "ViewDialogFragment");
    }
	
	/*@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		
		AlertDialog.Builder bulder = new AlertDialog.Builder(getActivity(),R.style.NoBorderDialog);
		LayoutInflater inflater = getActivity().getLayoutInflater();
		final View view = inflater.inflate(R.layout.pmc_login_tencent, null);
		bulder.setView(view);
		//使用方式：  
		
		*//*ViewDialogFragment dialog = new ViewDialogFragment();
		dialog.show(getFragmentManager());*//*

		return bulder.create();
	}
	*/

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		return super.onCreateDialog(savedInstanceState);
	}

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
	}
	
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
	
	
}
