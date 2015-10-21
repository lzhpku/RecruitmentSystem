package cn.edu.pku.recruitment.knowledgeBase;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * @author lzh@pku
 * @version 1.0
 * */

public class BaseBuilder {
	
	/** 职位数量 */
	public static int positionNumberM = 100;
	/** 技能数量 */
	public static int skillNumberM = 100;
	/** 输入文件编码方式 */
	public static final String encodingInput = "UTF-8";
	/** 记录技能出现次数 */
	public static Map <String, Integer> skillCounter = new HashMap <String, Integer> ();
	/** 记录职位出现次数 */
	public static Map <String, Integer> positionCounter = new HashMap <String, Integer> ();
	/** 向量化文件矩阵 */
	public static ArrayList<ArrayList <Double>> vectors = new ArrayList<ArrayList <Double>>();
	/** 向量化文件矩阵职位字符串 */
	public static ArrayList <String> vectorsP = new ArrayList <String>();
	/** 单词计数器 */
	public static int counter = 0;
	/** 单词计数区间 */
	public static int counterMax = 100000;
	/** 职位列表文件路径 */
	public static String positionListFilePath = "positionList.txt";
	/** 技能列表文件路径 */
	public static String skillListFilePath = "skillList.txt";
	/** 向量化文件存储路径 */
	public static String vectorFilePath = "vector.txt";
	/** 匹配分值文件路径 */
	public static String matchProbFilePath = "contribution.txt";
	/** 匹配分值矩阵 */
	public static double [][] matchProb;
	/** 相似分值文件路径 */
	public static String skillSimilarityFilePath = "similarity.txt";
	/** 相似分值矩阵 */
	public static double [][] skillSimilarity;
	
	public static FileOutputStream t1;
	public static OutputStreamWriter t2;
	public static BufferedWriter t3;
	
	/** 
	 * 设置职位列表文件参数 
	 * @param _positionNumberM 职位数量
	 * @param _positionListFilePath 职位列表文件路径
	 * */
	public static void setPositionFile(int _positionNumberM, String _positionListFilePath)
	{
		positionNumberM = _positionNumberM;
		positionListFilePath = _positionListFilePath;
	}
	
	/** 
	 * 设置技能列表文件参数 
	 * @param _skillNumberM 技能数量
	 * @param _skillListFilePath 技能列表文件路径
	 * */
	public static void setSkillFile(int _skillNumberM, String _skillListFilePath)
	{
		skillNumberM = _skillNumberM;
		skillListFilePath = _skillListFilePath;
	}
	
	/** 
	 * 设置向量化文件参数 
	 * @param _vectorFilePath 向量化文件路径
	 * */
	public static void vectorFile(String _vectorFilePath)
	{
		vectorFilePath = _vectorFilePath;
	}
	
	/** 
	 * 设置匹配分值文件参数 
	 * @param _matchProbFilePath 匹配分值文件路径
	 * */
	public static void setProbFile(String _matchProbFilePath)
	{
		matchProbFilePath = _matchProbFilePath;
	}
	
	/** 
	 * 设置相似分值文件参数 
	 * @param _skillSimilarityFilePath 相似分值文件路径
	 * */
	public static void setSimilFile(String _skillSimilarityFilePath)
	{
		skillSimilarityFilePath = _skillSimilarityFilePath;
	}
	
	/**
	 * 技能词汇提取，默认文本格式：第一行为职位标题，第二行为分词后的职位描述
	 * @param textPath 待处理文本路径
	 * */
	public static void skillExtraction(String textPath) throws IOException, FileNotFoundException
	{
		Pattern p = Pattern.compile("[a-zA-Z]");
		File text = new File(textPath);
		InputStreamReader isr = new InputStreamReader(new FileInputStream(text), encodingInput);
		@SuppressWarnings("resource")
		BufferedReader reader = new BufferedReader(isr);
		String line = null;
		try {
			line = reader.readLine();
			line = reader.readLine();
			String [] temp = line.split(" ");
			for(int i = 0; i < temp.length; i ++)
			{
				if(p.matcher(temp[i]).find())
				if((i - 1 >= 0 && (temp[i - 1].equals("熟悉") 
						|| temp[i - 1].equals("了解")
						|| temp[i - 1].equals("掌握")
						|| temp[i - 1].equals("精通")
						|| temp[i - 1].equals("具备")
						|| temp[i - 1].equals("编写")
						|| temp[i - 1].equals("使用")))
				|| (i + 1 < temp.length && (temp[i + 1].equals("编程") 
						|| temp[i + 1].equals("经验")
						|| temp[i + 1].equals("能力")
						|| temp[i + 1].equals("技术")
						|| temp[i + 1].equals("框架")
						|| temp[i + 1].equals("语言"))))
				{
					if(skillCounter.containsKey(temp[i]))
					{
						int newNum = skillCounter.get(temp[i]) + 1;
						skillCounter.remove(temp[i]);
						skillCounter.put(temp[i], newNum);
					}
					else
						skillCounter.put(temp[i], 1);
					counter ++;
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(counter > counterMax)
		{
			counter = 0;
			skillCounterCleanTail();
		}
	}
	
	/** 
	 * 批处理提取技能词汇
	 * @param srcDir 文件路径
	 */
	public static void batchSkillExtraction(String srcDir)
	{
		File [] files = new File(srcDir).listFiles();
		for(int i = 0; i < files.length; i ++)
		{
			if(files[i].isDirectory())
			{
				try {
					batchSkillExtraction(files[i].getAbsolutePath());
					}catch(Exception e){}
			}
			else
			{
				try {
					skillExtraction(files[i].getAbsolutePath());
					System.out.println(files[i].getAbsolutePath() + " finished");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	/** 
	 * 批处理提取技能词汇结束工作
	 * @throws IOException 
	 */
	public static void finishBatchSkillExtraction() throws IOException
	{
		t1 = new FileOutputStream(skillListFilePath);
		t2 = new OutputStreamWriter(t1);
		t3 = new BufferedWriter(t2);		
		@SuppressWarnings("rawtypes")
		Map.Entry [] set = getSortedHashMapByKey(skillCounter);
		skillCounter.clear();
		for(int i = 0; i < skillNumberM; i ++)
		{
			skillCounter.put(set[i].getKey().toString(), i);
			t3.write(set[i].getKey().toString());
			t3.newLine();
			System.out.println(set[i].getKey().toString() + "  " + i);
		}
		t3.close();
	}
	
	/**
	 * 去除低频技能词汇
	 * */
	public static void skillCounterCleanTail()
	{
		@SuppressWarnings("rawtypes")
		Map.Entry [] set = getSortedHashMapByKey(skillCounter);
		skillCounter.clear();
		for(int i = 0; i < skillNumberM; i ++)
			skillCounter.put(set[i].getKey().toString(), 
					Integer.parseInt(set[i].getValue().toString()));
	}
	
	/**
	 * HashMap排序器
	 * */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Map.Entry[] getSortedHashMapByKey(Map h)
	{
		Set set = h.entrySet();
		Map.Entry[] entries = (Map.Entry[]) set.toArray(new Map.Entry[set.size()]);
		Arrays.sort(entries, new Comparator() 
		{
			public int compare(Object arg0, Object arg1) 
			{
		        Object value1 = ((Map.Entry) arg0).getValue();
		        Object value2 = ((Map.Entry) arg1).getValue();
		        return ((Comparable) value2).compareTo(value1);
		    }
		});
		return entries;
	}

	/**
	 * 文本文件向量化
	 * @param textPath 待处理文本路径
	 * */
	public static void vectorExtraction(String textPath) throws IOException, FileNotFoundException
	{
		File text = new File(textPath);
		InputStreamReader isr = new InputStreamReader(new FileInputStream(text), encodingInput);
		@SuppressWarnings("resource")
		BufferedReader reader = new BufferedReader(isr);
		String line = null;
		line = reader.readLine();
		line = reader.readLine();
		String [] temp = line.split(" +");
		int [] vector = new int [skillNumberM];
		for(int i = 0; i < skillNumberM; i ++)
			vector[i] = 0;
		for(int i = 0; i < temp.length; i ++)
		{
			if(skillCounter.containsKey(temp[i]))
				vector[skillCounter.get(temp[i])] ++;
		}
		int maxValue = 0, maxIndex = 0;
		for(int i = 0; i < skillNumberM; i ++)
		{
			if(maxValue < vector[i])
			{
				maxValue = vector[i];
				maxIndex = i;
			}
		}
		String ret = new String();
		for(String str : skillCounter.keySet())
		{
			if(skillCounter.get(str) == maxIndex)
			{
				ret = ret + str;
				break;
			}
		}
		vectorsP.add(ret);
		ArrayList <Double> tempVector = new ArrayList <Double>();
		for(int i = 0; i < skillNumberM; i ++)
		{
			ret = ret + " " + String.valueOf(vector[i]);
			tempVector.add((double)vector[i]);
		}
		vectors.add(tempVector);
		t3.write(ret);
		t3.newLine();
	}
	
	/** 
	 * 批处理文本文件向量化
	 * @param srcDir 文件路径
	 */
	public static void batchVectorExtraction(String srcDir)
	{
		File [] files = new File(srcDir).listFiles();
		for(int i = 0; i < files.length; i ++)
		{
			if(files[i].isDirectory())
			{
				try {
					batchVectorExtraction(files[i].getAbsolutePath());
					}catch(Exception e){}
			}
			else
			{
				try {
					vectorExtraction(files[i].getAbsolutePath());
					System.out.println(files[i].getAbsolutePath() + " finished");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
		
	/**
	 * 批处理文本文件向量化初始化
	 * @throws FileNotFoundException 
	 * */
	public static void startBatchVectorExtraction() throws FileNotFoundException
	{
		t1 = new FileOutputStream(vectorFilePath);
		t2 = new OutputStreamWriter(t1);
		t3 = new BufferedWriter(t2);
	}
	
	/**
	 * 批处理文本文件向量化结束工作
	 * @throws IOException 
	 * */
	public static void finishBatchVectorExtraction() throws IOException
	{
		t3.close();
	}
	
	/**
	 * 职位词汇提取
	 * @throws IOException 
	 * */
	public static void positionExtraction(String textPath) throws IOException
	{
		File text = new File(textPath);
		InputStreamReader isr = new InputStreamReader(new FileInputStream(text), encodingInput);
		@SuppressWarnings("resource")
		BufferedReader reader = new BufferedReader(isr);
		String line = new String();
		while((line = reader.readLine()) != null)
		{
			String [] temp = line.split(" +");
			if(positionCounter.containsKey(temp[0]))
			{
				int newNum = positionCounter.get(temp[0]) + 1;
				positionCounter.remove(temp[0]);
				positionCounter.put(temp[0], newNum);
			}
			else
				positionCounter.put(temp[0], 1);
		}
		
		t1 = new FileOutputStream(positionListFilePath);
		t2 = new OutputStreamWriter(t1);
		t3 = new BufferedWriter(t2);
		@SuppressWarnings("rawtypes")
		Map.Entry [] set = getSortedHashMapByKey(positionCounter);
		positionCounter.clear();
		for(int i = 0; i < positionNumberM && i < set.length; i ++)
		{
			positionCounter.put(set[i].getKey().toString(), i);
			t3.write(set[i].getKey().toString());
			t3.newLine();
			System.out.println(set[i].getKey().toString() + "  " + i);
		}
		t3.close();
	}
	
	/**
	 * 构造相似分值矩阵 
	 * @throws IOException 
	 * */
	public static void makeSimilarityMatrix() throws IOException
	{
		double [][] m = new double [skillNumberM][skillNumberM];
		double [] average = new double [skillNumberM];
		double [] sigma = new double [skillNumberM];
		for(int i = 0; i < vectors.size(); i ++)
			for(int j = 0; j < average.length; j ++)
				average[j] = average[j] + (double) vectors.get(i).get(j);
		for(int j = 0; j < average.length; j ++)
			average[j] = average[j] / (double) (vectors.size());
		for(int i = 0; i < vectors.size(); i ++)
			for(int j = 0; j < average.length; j ++)
				sigma[j] = sigma[j] + (vectors.get(i).get(j) - average[j])
								* (vectors.get(i).get(j) - average[j]);
		for(int j = 0; j < average.length; j ++)
			sigma[j] = Math.sqrt(sigma[j] / (double) (vectors.size() - 1));
		for(int i = 0; i < average.length; i ++)
			for(int j = i; j < average.length; j ++)
			{
				if(i == j)
					m[i][j] = 1.0;
				else
				{
					for(int k = 0; k < vectors.size(); k ++)
						m[i][j] = (vectors.get(k).get(i) - average[i])
							* (vectors.get(k).get(j) - average[j])
							/ sigma[i] / sigma[j];
					m[i][j] = m[i][j] / (double) (vectors.size() - 1);
				}
			}
		
		t1 = new FileOutputStream(skillSimilarityFilePath);
		t2 = new OutputStreamWriter(t1);
		t3 = new BufferedWriter(t2);
		for(int i = 0; i < average.length; i ++)
		{
			for(int j = 0; j < average.length; j ++)
			{
				if(j < i)
					t3.write(m[j][i] + "	");
				else
					t3.write(m[i][j] + "	");
			}
			t3.newLine();
		}
		t3.close();
		System.out.println("similarity matrix completed");
	}
	
	/**
	 * 构造匹配分值矩阵 
	 * @throws IOException 
	 * */
	public static void makeMatchProbMatrix() throws IOException
	{
		double [][] m = new double [positionNumberM][skillNumberM];
		for(int i = 0; i < positionNumberM; i ++)
			for(int j = 0; j < skillNumberM; j ++)
				m[i][j] = 0.0;
		int [] vectorsTag = new int [vectors.size()];
		for(int i = 0; i < vectors.size(); i ++)
		{
			if(positionCounter.containsKey(vectorsP.get(i)))
				vectorsTag[i] = positionCounter.get(vectorsP.get(i));
			else
				vectorsTag[i] = (int) ((double) (positionNumberM - 1) * Math.random()) ;
		}
		for(int i = 0; i < vectors.size(); i ++)
			for(int j = 0; j < skillNumberM; j ++)
				m[vectorsTag[i]][j] = m[vectorsTag[i]][j] + vectors.get(i).get(j);
		double [] sumI = new double [positionNumberM];
		for(int i = 0; i < positionNumberM; i ++)
			for(int j = 0; j < skillNumberM; j ++)
				sumI[i] = sumI[i] + m[i][j];
		for(int i = 0; i < positionNumberM; i ++)
			for(int j = 0; j < skillNumberM; j ++)
				m[i][j] = (m[i][j] + 1.0) / (sumI[i] + 1.0);
		
		t1 = new FileOutputStream(matchProbFilePath);
		t2 = new OutputStreamWriter(t1);
		t3 = new BufferedWriter(t2);
		for(int i = 0; i < positionNumberM; i ++)
		{
			for(int j = 0; j < skillNumberM; j ++)
			{
				t3.write(m[i][j] + "	");
			}
			t3.newLine();
		}
		t3.close();
		System.out.println("match probability matrix completed");
	}
	
	/**
	 * 清除中间结果
	 * */
	public static void clean()
	{
		vectors.clear();
	}
	
	public static void main(String [] args) throws IOException
	{
		batchSkillExtraction("E:\\workj2ee\\智联招聘 训练集_pro");
		finishBatchSkillExtraction();
		startBatchVectorExtraction();
		batchVectorExtraction("E:\\workj2ee\\智联招聘 训练集_pro");
		finishBatchVectorExtraction();
		positionExtraction("vector.txt");
		makeSimilarityMatrix();
		makeMatchProbMatrix();
		clean();
	}
}
