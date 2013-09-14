package com.mars.dbexport.service.sortor;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;

public class GenericSortor implements Sortor {

	@Override
	public void sort(List<String> results) {
		if (CollectionUtils.isEmpty(results))
			return;
		int point = -1;
		for (String result : results) {
			if (!result.contains("/"))
				continue;
			String[] split = result.split(" ");
			for (int i = 0; i < split.length; i++) {
				if (split[i].contains("/")) {
					point = i;
					break;
				}
			}
			if (point >= 0)
				break;
		}
		if (point < 0)
			return;
		String tmp = null;
		for (int i = results.size() - 1; i > 0; --i)
			for (int j = 0; j < i; ++j) {
				String[] split1 = results.get(j).split(" ");
				String[] split2 = results.get(j + 1).split(" ");
				if (split1.length < (point + 1) || split2.length < (point + 1))
					continue;
				String[] split3 = split1[point].split("/");
				String[] split4 = split2[point].split("/");
				for (int k = 0; k < split3.length && k < split4.length; k++) {
					if (split3[k].equals(split4[k]))
						continue;
					int p1 = -1;
					int p2 = -1;
					try {
						p1 = Integer.parseInt(split3[k]);
					} catch (Exception ex) {
					}
					try {
						p2 = Integer.parseInt(split4[k]);
					} catch (Exception ex) {
					}
					if (p1 > p2) {
						tmp = results.get(j);
						results.set(j, results.get(j + 1));
						results.set(j + 1, tmp);
						break;
					} else {
						break;
					}
				}
			}

	}

}
