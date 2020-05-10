package com.hg.anDGS;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

class TextHelper {
	String GetText(InputStream in) {
		String text = "";
		BufferedReader reader;
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
			reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
		} else {
			reader = new BufferedReader(new InputStreamReader(in, Charset.forName("UTF-8")));
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
