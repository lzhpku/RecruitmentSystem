package cn.edu.pku.recruitment.classifier;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * 分类器
 * @author lzh@pku
 * @version 1.0
 * */
public class Classifier {
	
	/** 分类器职位类别数量 */
	public static int positionNumberP = 15;
	/** 分类器职位特征数量 */
	public static int skillNumberP = 40;
	/** 分类器参数文件路径 */
	public static String predictProbFilePath = "classifier param.txt";
	/** 分类器参数矩阵 */
	public static double [][] predictProb = new double [positionNumberP][skillNumberP + 1];

	/**
	 * 自定义分类器参数
	 * @param _positionNumberP 分类器职位类别数量
	 * @param _skillNumberP 分类器技能数量
	 * @param _predictProbFilePath 分类器参数文件路径
	 * */
	public static void setProbFile(int _positionNumberP, int _skillNumberP, String _predictProbFilePath)
	{
		positionNumberP = _positionNumberP;
		skillNumberP = _skillNumberP;
		predictProbFilePath = _predictProbFilePath;
	}
	
	/**
	 * 加载分类器
	 * @exception IOException 找不到分类器参数文件
	 * */
	public static void loadModel() throws IOException
	{
		predictProb = null;
		predictProb = new double [positionNumberP][skillNumberP + 1];
		
		InputStreamReader isr = new InputStreamReader(new FileInputStream(predictProbFilePath));
		BufferedReader reader = new BufferedReader(isr);
		String line = null;
		int counter = 0;
		while((line = reader.readLine()) != null && counter < positionNumberP)
		{
			String [] temp = line.trim().split("	");
			for(int i = 0; i < skillNumberP + 1; i ++)
				predictProb[counter][i] = Double.parseDouble(temp[i]);
			counter ++;
		}
//		for(int i = 0; i < skillNumberP + 1; i ++)
//		System.out.println(skillList[i]);
//		System.out.println("预测概率矩阵");
		reader.close();
	}
	
	/**
	 * 获取技能向量所属的职位类别分布
	 * @param skillVector 技能向量
	 * @return 所属职位类别的分布
	 * */
	public double [] getDistri(int [] skillVector)
	{
		double [] rowCoefficientSum = new double [positionNumberP];
		double allSum = 0.0;
		for(int i = 1; i < positionNumberP; i ++)
		{
			rowCoefficientSum[i] = predictProb[i][0];
			for(int j = 1; j < skillNumberP + 1; j ++)
				rowCoefficientSum[i] += (double)skillVector[j - 1] * predictProb[i][j];
			rowCoefficientSum[i] = Math.exp(rowCoefficientSum[i]);
			allSum = allSum + rowCoefficientSum[i];
		}
		rowCoefficientSum[0] = 1.0 / (1.0 + allSum);
		for(int i = 1; i < positionNumberP; i ++)
			rowCoefficientSum[i] = rowCoefficientSum[i] / (1.0 + allSum);
		return rowCoefficientSum;
	}
}
