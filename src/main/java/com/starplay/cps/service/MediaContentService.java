package com.starplay.cps.service;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Scanner;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;

@Service
public class MediaContentService implements IMediaContentService {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.starplay.cps.services.IMediaContentService#getMedia()
	 */
	public String getMedia(String filter, String level) {
		if (!level.equals("censored") || !level.equals("uncensored")) {
			throw new IllegalArgumentException("Provide correct value for level field");
		}
		String media = null;
		String json = getJsonFromUrl(
				"https://de8a7d97-b45e-401b-b30e-39ae7b922405.mock.pstmn.io/api/v1.0/mediaCatalog/titles/movies");
		media = parseJason(json, filter, level);
		return media;
	}

	private String getJsonFromUrl(String uri) {

		String json = "";
		try {
			URL url = new URL(uri);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.connect();
			int responsecode = conn.getResponseCode();
			System.out.println("Response code is: " + responsecode);
			if (responsecode != 200)
				throw new RuntimeException("HttpResponseCode: " + responsecode);
			else {
				Scanner sc = new Scanner(url.openStream());
				while (sc.hasNext()) {
					json += sc.nextLine();
				}
				sc.close();
			}
			conn.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return json;

	}

	private String parseJason(String json, String filter, String level) {
		JSONParser parse = new JSONParser();
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject = (JSONObject) parse.parse(json);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return RetrieveMedia(jsonObject, level);
	}

	private String RetrieveMedia(JSONObject obj, String level) {
		JSONArray entries = (JSONArray) obj.get("entries");
		List<JSONObject> censoredEntries = getCensoredEntry(entries, level);
		String finalMedia = null;
		for (JSONObject censoredEntry : censoredEntries) {
			JSONArray mediaList = (JSONArray) censoredEntry.get("media");
			finalMedia = getFinalMediaContent(mediaList, level);
		}
		return finalMedia;
	}

	private List<JSONObject> getCensoredEntry(JSONArray entries, String level) {
		Predicate<JSONObject> pred = (entry) -> entry.get("peg$contentClassification").equals("Censored");

		Stream<JSONObject> entryStream = entries.stream();
		List<JSONObject> censoredEntries = entryStream.filter(pred).collect(Collectors.toList());
		return censoredEntries;

	}

	private String getFinalMediaContent(JSONArray mediaList, String level) {

		Predicate<JSONObject> predMedia = (media) -> {
			String guid = media.get("guid").toString();
			return guid.endsWith("C") && level.equals("censored") || !guid.endsWith("C") && level.equals("uncensored");
		};
		Stream<JSONObject> mediaStream = mediaList.stream();
		List<JSONObject> filteredMedia = mediaStream.filter(predMedia).collect(Collectors.toList());
		System.out.println(filteredMedia);
		return filteredMedia.toString();
	}
}
