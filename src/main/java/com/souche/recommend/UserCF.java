package com.souche.recommend;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class UserCF {
	public static void main(String[] args) {
		Map<String,Set<String>> trainData = getTrainData();
		Map<String,Integer> user_id = getUserId(trainData);
		
		int S = trainData.size();
		int[][] sparseMatrix = new int[S][S];
		//倒排表
		Map<String,Set<String>> item_users = getItemUsers(trainData);
		//相关用户集合
		Map<String,Set<String>> relatedUserMap = new HashMap<String,Set<String>>();
		int[] N = new int[S];
		for(Entry<String,Set<String>> entry:item_users.entrySet()) {
			for(String u:entry.getValue()) {
				N[user_id.get(u)] += 1;
				for(String v:entry.getValue()) {
					if(u.equals(v)) {
						continue;
					}
					sparseMatrix[user_id.get(u)][user_id.get(v)] += 1;
					if(relatedUserMap.get(u) != null) {
						relatedUserMap.get(u).add(v);
					} else {
						Set<String> relatedUser = new HashSet<String>();
						relatedUser.add(v);
						relatedUserMap.put(u, relatedUser);
					}
				}
			}
		}
		
		//用户相似度矩阵
		double[][] W = new double[S][S];
		for(String u:relatedUserMap.keySet()) {
			for(String v:relatedUserMap.get(u)) {
				int uid = user_id.get(u);
				int vid = user_id.get(v);
				//余弦相似度
				W[uid][vid] = sparseMatrix[uid][vid] / (Math.sqrt(N[uid] * N[vid]));
			}
		}
		
		String recUser = "A";
		Set<String> item_hasd = trainData.get(recUser);
		Map<String,Float> rank = new HashMap<String,Float>();
		//没有对相关用户做相似度排名
		for(String v:relatedUserMap.get(recUser)) {
			for(String i:trainData.get(v)) {
				if(item_hasd.contains(i)) {
					continue;
				}
				if(rank.get(i) != null) {
					float score = rank.get(i);
					score += W[user_id.get(recUser)][user_id.get(v)];
					rank.put(i, score);
				} else {
					rank.put(i, (float) W[user_id.get(recUser)][user_id.get(v)]);
				}
			}
		}
		for(Entry<String,Float> entry:rank.entrySet()) {
			System.out.println(entry.getKey() + " " + entry.getValue());
		}
		
	}

	//辅助表
	private static Map<String,Integer> getUserId(Map<String,Set<String>> trainData) {
		Map<String,Integer> user_id = new HashMap<String,Integer>();
		int id = 0;
		for(String user:trainData.keySet()) {
			user_id.put(user, id);
			id++;
		}
		
		return user_id;
	}
	
	//物品用户倒排表
	private static Map<String,Set<String>> getItemUsers(Map<String,Set<String>> trainData) {
		Map<String,Set<String>> item_users = new HashMap<String,Set<String>>();
		for(Entry<String,Set<String>> entry:trainData.entrySet()) {
			for(String item:entry.getValue()) {
				if(item_users.get(item) != null) {
					item_users.get(item).add(entry.getKey());
				} else {
					Set<String> users = new HashSet<String>();
					users.add(entry.getKey());
					item_users.put(item, users);
				}
			}
		}
		
		return item_users;
	}
	
	private static Map<String,Set<String>> getTrainData() {
		Map<String,Set<String>> train = new HashMap<String,Set<String>>();
		String[] a = {"a","b","d"};
		String[] b = {"a","c"};
		String[] c = {"b","e"};
		String[] d = {"c","d","e"};
		Set<String> A = getUserSet(a);
		Set<String> B = getUserSet(b);
		Set<String> C = getUserSet(c);
		Set<String> D = getUserSet(d);
		train.put("A", A);
		train.put("B", B);
		train.put("C", C);
		train.put("D", D);
		
		return train;
	}
	
	private static Set<String> getUserSet(String[] userArr) {
		Set<String> userSet = new HashSet<String>();
		for(String u:userArr) {
			userSet.add(u);
		}
		
		return userSet;
	}

}
