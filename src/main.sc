require: entities.sc
    module = sys.zfl-common
    
require: patterns.sc
    module = sys.zfl-common
    
require: audiofiles.yaml
    var = audiofiles

require: general.sc 

theme: /

    state: Start
        script: 
            $jsapi.startSession();
        q!: $regex</start>
        go!: /allo

    state: allo || modal = true
        audio: {{ audiofiles.allo }} || name = "Алло"
        script:
            $session.time_create = $jsapi.dateForZone("Europe/Moscow", "dd.MM.YY HH:mm");
            $dialer.bargeInResponse({
                bargeIn: "phrase", 
                bargeInTrigger: "final", 
                noInterruptTime: 1 
            })

        state: autoanswer
            q: $autoanswer
            intent: /АВТООТВЕТЧИК
            q: Ваш звонок является * в очереди и будет обслужен * доступным оператором
            script:
                $dialer.hangUp();
            go!: /endOfScript

        state: fallback
            event: noMatch
            go!: /Hello

    state: Hello
        audio: {{ audiofiles.greetings }} || name = "Добрый день, меня зовут Татьяна, я из компании Ромашка. Скажите, у вас есть минутка?"

        state: busy
            intent: /ПЕРЕЗВОНИТЬ_ЗАНЯТ
            audio: {{ audiofiles.busy }} || name = "Когда я могу вам перезвонить?"
            
            state: noMatch
                event: noMatch
                audio: {{ audiofiles.busyBye }} || name = "Спасибо за уделенной время, хорошего дня!"
                script: 
                    $dialer.hangUp();
                go!: /endOfScript

        state: noBusiness
            intent: /НЕТ_БИЗНЕСА
            audio: {{ audiofiles.noBusiness }} || name = "Что ж, еще не поздно начать свое дело, если вам понадобятся чат боты, обращайтесь. До свидания"
            script: 
                $dialer.hangUp();
            go!: /endOfScript

        state: agreement
            intent: /СОГЛАСИЕ
            go!: /general/general
            

        state: callBack
            intent: /ПЕРЕЗВОНЮ
            audio: {{ audiofiles.callBack }} || name = "Вы можете позвонить нам по номеру 8800ххххххх, я буду ждать звонка. Спасибо за уделенное время, хорошего дня!"
            script: 
                $dialer.hangUp();
            go!: /endOfScript

        state: autoanswer
            q: $autoanswer
            intent: /АВТООТВЕТЧИК
            q: Ваш звонок является * в очереди и будет обслужен * доступным оператором
            script:
                $dialer.hangUp();
            go!: /endOfScript

        state: rejection
            intent: /ОТКАЗ
            audio: {{ audiofiles.totalRejection }} || name = "Хорошо, я поняла вас. До свидания"
            script: 
                $dialer.hangUp();
            go!: /endOfScript

    state: endOfScript
        event!: hangup
        script: 
            $session.attempt = 1;
            $session.history = $jsapi.chatHistory();
            $dialer.reportData("History", $session.history || "-");
            if ($request.channelType !== "chatwidget" || !testMode()) {
                $dialer.reportData("Recording", $session.recordingURL || "-");
                if ($session.lead == 1) {
                    $integration.googleSheets.writeDataToLine( 
                        "АПИ-токен интеграции",
                        "АПИ-Токен таблицы",
                        "Лист тпблицы",
                        ["Лид. Отправить КП", $dialer.getCaller() || "", $dialer.getPayload().org_name || "", $session.time_create || "", $session.history || "", $session.recordingURL || "-"]
                    );
                }
            }
            $dialer.hangUp()
