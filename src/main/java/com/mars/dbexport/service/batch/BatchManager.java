package com.mars.dbexport.service.batch;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

import org.slf4j.Logger;

import com.mars.dbexport.AppContext;
import com.mars.dbexport.service.LogFactory;

/**
 * <p>
 * Title: BatchManager
 * </p>
 * <p>
 * Description:
 * </p>
 * 
 * @author Yao Liqiang
 * @created 2011-11-10 ����10:09:08
 * @modified [who date description]
 * @check [who date description]
 */
public abstract class BatchManager {
	protected Logger logger = AppContext.getLogFactory().getLogger(
			LogFactory.LOG_ERROR);
	private Set<String> iplist = new HashSet<String>();
	private Executor exec = Executors.newFixedThreadPool(AppContext
			.getAppParamters().getMaxThread());
	private List<FutureTask<BatchResult>> tasklist = new ArrayList<FutureTask<BatchResult>>();
	private List<BatchResult> results = new ArrayList<BatchResult>();

	public BatchManager(Set<String> iplist) {
		if (iplist != null)
			this.iplist = iplist;
	}

	public List<BatchResult> execute() {
		for (final String ip : iplist) {
			FutureTask<BatchResult> task = new FutureTask<BatchResult>(
					new Callable<BatchResult>() {
						@Override
						public BatchResult call() throws Exception {
							BatchResult result = new BatchResult();
							try {
								result = runTask(ip);
							} catch (Exception ex) {
								result.setException(ex);
							}
							result.setIpAddr(ip);
							return result;
						}
					});

			tasklist.add(task);
		}

		for (FutureTask<BatchResult> task : tasklist) {
			exec.execute(task);
		}

		while (tasklist.size() > 0) {
			int size = tasklist.size();
			for (int k = 0; k < size; k++) {
				FutureTask<BatchResult> task = tasklist.get(k);
				if (task.isDone()) {
					try {
						BatchResult result = task.get();
						results.add(result);
					} catch (Exception ex) {
					}
					tasklist.remove(task);
					break;
				}
			}
		}
		return results;
	}

	protected abstract BatchResult runTask(String ip);

}
