from flask import Flask, request, jsonify
from openai import OpenAI
# ^ had to python -m pip install flask + python -m pip install openai 

app = Flask(__name__)
# i got this key by going to openAI.com and requesting a private api key 
client = OpenAI(api_key="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")

@app.route("/summary", methods=["POST"])
# this method recieves a POST HTTP request (JSON format of tasks that need to be completed on a given day)
def generate_summary():
    data = request.get_json()
    tasks = data.get("tasks", [])
# THIS IS MODIFYABLE -- could be updated more to generate better summaries, the more info and examples the better for the LLM 
    prompt = f"""
You are an executive assistant. Your job is to recap today's tasks clearly and helpfully.

Format:
- A warm greeting
- 2 sentences summarizing the priorities
- Mention each task by name

Example:
Tasks: ["Headset", "PTT", "TAK server running"]
Output: "Good morning! Here's what's on your plate today. Start by completing the Headset task, then move on to the PTT and confirm that the TAK server is running properly. You've got a productive day ahead!"

Now generate a summary for the following tasks:
{tasks}
"""

    response = client.chat.completions.create(
        model="gpt-4.1-mini",
        messages=[{"role": "user", "content": prompt}]
    )

    summary_text = response.choices[0].message.content
    return jsonify({"summary": summary_text})

if __name__ == "__main__":
    app.run(host="0.0.0.0", port=5000, debug=True)