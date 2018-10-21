import os
import json

def diff_files(file_1, file_2):
	content_1 = ""
	with open(file_1, "r") as f:
		while True:
			line = f.readline()
			if line == None or len(line) == 0:
				break
			content_1 += line
	content_2 = ""
	with open(file_2, "r") as f:
		while True:
			line = f.readline()
			if line == None or len(line) == 0:
				break
			content_2 += line
	return content_1 == content_2

if __name__ == "__main__":
	#file_1 = "./src/test/Test.lv.out"
	#file_2 = "./my_results/Test.lv.out"
	#print diff_files(file_1, file_2)
	os.system("make clean")
	os.system("make")
	my_results_dir = "./my_results"
	os.system("rm -r my_results")
	os.system("mkdir my_results")
	with open("./test_cases.json", "r") as f:
		test_cases = json.load(f)
	for test_case in test_cases:
		command = "./run.sh flow.Flow submit.MySolver %s %s > %s/%s" % (test_case["analysis"], test_case["test_case"], my_results_dir, test_case["output"])
		#print command
		os.system(command)
		file_1 = my_results_dir + "/" + test_case["output"]
		file_2 = "./src/test/" + test_case["output"]
		if diff_files(file_1, file_2):
			print "TestCase: %s %s ----> Correct!" % (test_case["analysis"], test_case["test_case"])
		else:
			print "TestCase: %s %s ----> Wrong!" % (test_case["analysis"], test_case["test_case"])
