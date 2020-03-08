import firebase_admin
from firebase_admin import credentials
from firebase_admin import firestore

cred = credentials.Certificate(
    'getadate-7aa07-firebase-adminsdk-ssuwr-2944e24fd4.json')
firebase_admin.initialize_app(cred)

db = firestore.client()

docs = db.collection(u"characteristics").stream()

all_ques = [[], []]
for doc in docs:
    docID = doc.id
    ques = db.collection(u"questions").where(u"cId", u"==", docID).stream()
    all_ques[int(doc.to_dict()["gender"])].append(ques)

print("Done got all questions ....")

for qs in all_ques[0]:
    for q in qs:
        db.collection("questions").document(q.id).update({u"gender": 0})

print("Done set all female genders ....")

for qs in all_ques[1]:
    for q in qs:
        db.collection("questions").document(q.id).update({u"gender": 1})

print("Done set all male genders ....")

print("Done all successfully ....")
