package com.starplay.cps.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.starplay.cps.service.IMediaContentService;

@RestController
@RequestMapping("/media")
public class MediaContentController {

	@Autowired
	IMediaContentService service;

	@RequestMapping(value = "/get", method = RequestMethod.GET)
	public @ResponseBody String getMedia(@RequestParam String filter, @RequestParam String level) {
		if (filter == null || level == null) {
			throw new IllegalArgumentException("Filter and level should value not be null");
		}
		String mediaContent = null;
		if (filter.equals("censoring")) {
			mediaContent = service.getMedia(filter, level);
		}
		return mediaContent;
	}
}
