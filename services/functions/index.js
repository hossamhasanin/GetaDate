const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);
const db = admin.firestore();

exports.sendNotification = functions.firestore.document("users/{user_id}").onCreate((snap , context) => {
    const name = snap.data().username;
    const age = snap.data().age.toString();
    const gender = snap.data().gender;

    const maleTopic = "male";
    const femaleTopic = "female";

    const notificationTitle = "هناك اناس جدد يمكنك التعرف عليهم"
    const notificationBody = "الاسم " + name

    const payload = {
            notification: {
                title: notificationTitle,
                body: notificationBody,
                icon: "default"
            },
            data: {
                noti_type: "newUser",
                username : snap.data().username,
                age : snap.data().age.toString(),
                location: snap.data().location.join(","),
                email : snap.data().email,
                online: snap.data().online.toString(),
                gender: snap.data().gender.toString(),
                id: snap.data().id,
            }
        }

    var finalTopic = ""
    if (gender == 1){
        finalTopic = maleTopic
    } else {
        finalTopic = femaleTopic
    }

    return admin.messaging().sendToTopic(finalTopic , payload).then(result => {
        console.log("Notification sent successfully " + finalTopic);
    }).catch(err => {
        console.log("Error : " + err);
    });

    //console.log("Hello !");

});


exports.datingRequest = functions.firestore.document("datingRequests/{id}").onCreate((snap , context) => {
    const to = snap.data().to;
    const from = snap.data().from;

    return db.collection("users").doc(from).get().then( doc => {
        if (doc.exists){
            let username = doc.data().username

            return db.collection("users").doc(to).get().then(toDoc => {
                 let token_id = toDoc.data().token_id

                 const notificationTitle = "هناك احدهم يود الخروج في موعد معك"
                 const notificationBody = "الاسم " + username

                 const payload = {
                       notification: {
                                      title: notificationTitle,
                                      body: notificationBody,
                                      icon: "default"
                                     },
                                     data: {
                                           noti_type: "datingRequest",
                                           username : doc.data().username,
                                           age : doc.data().age.toString(),
                                           location: doc.data().location.join(","),
                                           email : doc.data().email,
                                           online: doc.data().online.toString(),
                                           gender: doc.data().gender.toString(),
                                           id: "none",
                                           }
                                }


                 return admin.messaging().sendToTopic(toDoc.data().id , payload).then(result => {
                                     console.log("Notification sent successfully " + token_id);
                                 }).catch(err => {
                                     console.log("Error : " + err);
                                 });



            }).catch(err => {
                console.log("Error : " + err);
            });

        }
    }).catch(err => {
         console.log('Error getting document', err);
    });


});

exports.PlaceOrTimeNotAccepted = functions.firestore.document("datingRequests/{id}").onUpdate((snap , context) => {
    const to = snap.after.data().to;
    const from = snap.after.data().from;

    if ((snap.after.data().placeAccepted != snap.before.data().placeAccepted || snap.after.data().timeAccepted != snap.before.data().timeAccepted) && (!snap.after.data().placeAccepted || !snap.after.data().timeAccepted)){
        return db.collection("users").doc(from).get().then( doc => {
                if (doc.exists){
                    return db.collection("users").doc(to).get().then(toDoc => {
                         let token_id = toDoc.data().token_id

                         let notificationTitle = ""
                         let notificationBody = ""
                         let toId = ""
                         let username = ""



                         if (!snap.after.data().placeAccepted){
                             if (doc.data().gender == 1){
                                 toId = doc.data().id
                                  username = doc.data().username
                             } else {
                                 toId = toDoc.data().id
                                 username = toDoc.data().username
                             }
                             notificationTitle = "لم يتم الموافقة على المكان"
                             notificationBody = "لقد طلب " + username + " تغيير المكان"
                         } else if(!snap.after.data().timeAccepted) {
                             if (doc.data().gender == 0){
                                 toId = doc.data().id
                                 username = doc.data().username
                             } else {
                                 toId = toDoc.data().id
                                 username = toDoc.data().username
                             }
                             notificationTitle = "لم يتم الموافقة على الوقت"
                             notificationBody = "لقد طلب " + username + " تغيير الوقت"
                         }

                         const payload = {
                               notification: {
                                              title: notificationTitle,
                                              body: notificationBody,
                                              icon: "default"
                                             },
                                             data: {
                                                   noti_type: "PlaceOrTimeNotAccepted",
                                                   username : doc.data().username,
                                                   id: "none",
                                                   }
                                        }

                         return admin.messaging().sendToTopic(toId , payload).then(result => {
                                             console.log("Notification sent successfully " + token_id);
                                         }).catch(err => {
                                             console.log("Error : " + err);
                                         });



                    }).catch(err => {
                        console.log("Error : " + err);
                    });

                }
            }).catch(err => {
                 console.log('Error getting document', err);
            });
    } else {
        return
    }


});

exports.datingRequestChange = functions.firestore.document("datingRequests/{id}").onUpdate((snap , context) => {
    const to = snap.after.data().to;
    const from = snap.after.data().from;
    const requestId = context.params.id

                return db.collection("users").doc(from).get().then( doc => {
                    if (doc.exists){
                        return db.collection("users").doc(to).get().then(toDoc => {
                             let token_id = toDoc.data().token_id

                             let notificationTitle = ""
                             let notificationBody = ""
                             let toId = ""
                             let username = ""
                             let noti_type = ""



                             if (snap.after.data().place != snap.before.data().place){
                                 if (doc.data().gender == 0){
                                     toId = from
                                      username = toDoc.data().username
                                 } else {
                                     toId = to
                                     username = doc.data().username
                                 }
                                 noti_type = "placeChanged"
                                 notificationTitle = "لقد تم تغيير المكان"
                                 notificationBody = "لقد قام " + username + " بتغيير المكان"
                             } else if(snap.after.data().time != snap.before.data().time || snap.after.data().date != snap.before.data().date) {
                                 if (doc.data().gender == 1){
                                     toId = from
                                     username = toDoc.data().username
                                 } else {
                                     toId = to
                                     username = doc.data().username
                                 }
                                 noti_type = "timeChanged"
                                 notificationTitle = "لقد تم تغيير الوقت"
                                 notificationBody = "لقد قام " + username + " بتغيير الوقت"
                             } else if (snap.after.data().status != snap.before.data().status){
                                toId = from
                                username = doc.data().username
                                noti_type = "timeChanged"
                                notificationTitle = "تم الموافقة على طلب الخروجة"
                                notificationBody = "لقد قام " + username + " بالموافقة على طلب الخروجة"
                             }

                             if (toId != ""){
                                 const payload = {
                                       notification: {
                                                      title: notificationTitle,
                                                      body: notificationBody,
                                                      icon: "default"
                                                     },
                                                     data: {
                                                           noti_type: noti_type,
                                                           username : username,
                                                           id: "none",
                                                           requestId: requestId
                                                           }
                                                }

                                 return admin.messaging().sendToTopic(toId , payload).then(result => {
                                                     console.log("Notification sent successfully " + toId);
                                                 }).catch(err => {
                                                     console.log("Error " + toId + " : " + err);
                                                 });
                             }


                        }).catch(err => {
                            console.log("Error : " + err);
                        });

                    }
                }).catch(err => {
                     console.log('Error getting document', err);
                });


});