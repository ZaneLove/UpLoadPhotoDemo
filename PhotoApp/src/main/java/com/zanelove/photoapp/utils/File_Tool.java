package com.zanelove.photoapp.utils;

import android.os.Environment;

import java.io.*;

public class File_Tool {

	public static String SDPATH = Environment.getExternalStorageDirectory() + "/Zane/photo/";

	/**
	 * 在SD卡上创建目录；
	 */
	public static File createDIRFrom(String dirpath) {
		String SDPATH = Environment.getExternalStorageDirectory() + "/Zane/";
		File dir = new File(SDPATH + dirpath);
		dir.mkdir();
		return dir;
	}
}