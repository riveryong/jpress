/**
 * Copyright (c) 2015-2016, Michael Yang 杨福海 (michael@jpress.io).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.jpress.controller.front;

import io.jpress.core.JBaseController;
import io.jpress.template.TemplateUtils;

import com.jfinal.kit.PathKit;

public class BaseFrontController extends JBaseController {
	private static final String T_FORMAT = "/templates/%s/%s";

	public void render(String name) {
		do {
			if (templateExists(name)) {
				break;
			}
			name = clearProp(name);
		} while (name.contains("_"));

		if (!templateExists(name)) {
			renderText(String.format(
					"there is no \"%s\" file in template \"%s\".", name,
					TemplateUtils.getTemplateName()));
		} else {
			super.render(String.format(T_FORMAT,
					TemplateUtils.getTemplateName(), name));
		}
	}

	public String clearProp(String fname) {
		return fname.substring(0, fname.lastIndexOf("_")) + ".html";
	}

	private boolean templateExists(String htmlFileName) {
		String tName = TemplateUtils.getTemplateName();
		String htmlPath = String.format(T_FORMAT, tName, htmlFileName);
		return TemplateUtils.exists(PathKit.getWebRootPath() + htmlPath);
	}

}
