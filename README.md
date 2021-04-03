### AlarmApp(알람시계 앱)
**Coroutine + Background + Android Local DataBase를 사용해보고자 만들어본 앱**

---

#### 사용 기술
* **디자인패턴 : MVVM**
* **Dababase : Room**
* **기타 : Coroutine, LiveData, ViewModel, WorkManager, AlarmManager**

---

#### 구현 시 활용한 방법
* **액티비티와 프래그먼트 간 공통으로 필요한 데이터는 부모 액티비티의 ViewModel을 자식 프래그먼트와 공통으로 사용함으로써 LiveData로 관리**
* **정확한 시간에 알람이 울렸어야 했기 때문에 "Android Developers"의 백그라운드 처리 가이드를 참고하여 AlarmManger를 사용해 알람을 예약**
* **다른 사용자와 데이터를 주고받을 일이 없었기 때문에 서버를 구축하지 않고 안드로이드의 내장 Database(Room)를 활용하여 알람정보들을 저장**
* **알람이 울릴 시간이 되면 Notification과 함께 WorkManager를 활용하여 백그라운드에서 알람벨소리를 30초동안 울리게 하였고 화면이 꺼진 상태였다면 알람을 종료 할 수 있는 액티비티를 호출해 사용자가 알람을 종료할 수 있게함** 
