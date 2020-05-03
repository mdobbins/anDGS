package com.hg.anDGS;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

class TextHelper {
	String GetText(InputStream in) {
		String text = "";
		BufferedReader reader;
		try {
			reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			reader = new BufferedReader(new InputStreamReader(in));
		}
		StringBuilder sb = new StringBuilder();
		String line;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line).append("\n"); //new String(reader.readLine().getBytes(),"UTF-8"
			}
			text = sb.toString();
		} catch (Exception ignore) {

		} finally {
			try {

				in.close();
			} catch (Exception ignore) {
			}
		}
		return text;
	}
/*
	String GetText(HttpResponse response) {
		String text = "";
		try {
			text = GetText(response.getEntity().getContent());
		} catch (Exception ignore) {
		}
		return text;
	}
	+/
 */
}
