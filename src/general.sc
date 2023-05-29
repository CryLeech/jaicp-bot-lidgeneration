theme: /general
    
    state: general
        audio: {{ audiofiles.offer }} || name = 'Мы занимаемся разработкой входящих и исходящих голосовых и чат ботов для бизнеса. Скажите, хотели бы вы узнать подробнее?'
        go!: /generalIntents

    state: generalIntents
        state: chatBot
            intent: /ЧАТ-БОТ
            audio: {{ audiofiles.chatBot }} || name = 'Мы можем создать чат бота для любых целей и интегрировать его во все популярные мессенджеры, социальные сети или же на ваш сайт. Также мы сможем сделать интеграцию в crm для вашего удобства'
            go!: /general/general/agreement

        state: voiceBot
            intent: /ГОЛОСОВОЙ
            audio: {{ audiofiles.voiceBot }} || name = 'Мы создаем голосовых ботов, не отличимых от настоящих людей. Для этого мы используем нейронную модель распознавания, чтобы мы могли ответить на все вопросы клиента'
            go!: /general/general/agreement

        state: agreement
            intent: /СОГЛАСИЕ
            audio: {{ audiofiles.contact }} || name = 'Могу я передать ваш контакт нашему менеждеру, чтобы он связался с вами и смог подобрать вам подходящее предложение?'

            state: secondAgreement
                intent: /СОГЛАСИЕ
                audio: {{ audiofiles.lead}} || name = 'Хорошо, наш менеджер свяжется с вами в ближайщее время. До свидания'
                script: 
                    $dialer.hangUp;
                go!: /endOfScript
            
            state: notSuitable
                intent: /ОТКАЗ
                intent: /НЕТ_БИЗНЕСА
                event: noMatch
                audio: {{ audiofiles.understandRejection }} || name = 'Поняла вас, всего доброго, до свидания'

        state: personalData
            intent: /ПЕРСОНАЛЬНЫЕ_ДАННЫЕ
            audio: {{ audiofiles.personalData }} || name = 'Мы используем инфраструктуру российских компаний Selectel и Just AI, соблюдаем все требования Роскомнадзора и ФЗ-153 "О персональных данных", поэтому персональные данные ваших клиентов будут в безопасности'
            go!: /general/chooseBot

        state: notBoss
            intent: /НЕ_ЛПР
            audio: {{ audiofiles.notBoss }} || name = 'Подскажите, пожалуйста, номер телефона, с кем я могу поговорить на тему автоматизации бизнес-процессов?'

            state: rejection
                intent: /ОТКАЗ
                audio: {{ audiofiles.understandRejection }} || name = 'Поняла вас, всего доброго, до свидания'
                script:
                    $dialer.hangUp;
                go!: /endOfScript

            state: phoneNumber
                event: noMatch
                script:
                    var lastRequest = $request.query;
                    var dop = "7";
                    lastRequest = lastRequest.replace(/[^+\d]/g, '')
                    lastRequest = lastRequest.trim();
                    if ($session.str.length < 10) {
                        $session.str += lastRequest;
                        if ($session.str.length > 10) {
                            $reactions.audio(audiofiles.notBossLead);
                            $dialer.hangUp();
                            $reactions.transition('/endOfScript');
                        } 
                        else if ($session.str[0] == "9") {
                            $session.str = dop + $session.str;
                            $reactions.audio(audiofiles.notBossLead);
                            $dialer.hangUp();
                            $reactions.transition('/endOfScript');
                        } else  {
                            $reactions.transition('/general/genaralIntents/notBoss/phoneNumber');
                        }
                    }
                    else {
                        $reactions.audio(audiofiles.notBossLead);
                        $dialer.hangUp();
                        $reactions.transition('/endOfScript');
                    }

        state: outgoing
            intent: /ИСХОДЯЩИЙ
            audio: {{ audiofiles.outgoing }} || name = 'Это может быть бот лидогенератор,  оценщик CSI или же же просто бот уведомлящий клиентов об акциях, записи или времени начала мероприятия.'
            go!: /general/general/agreement

        state: incoming
            intent: /ВХОДЯЩИЙ
            audio: {{ audiofiles.incoming }} || name = 'Мы может сделать вам бот или IVR для распределния по отделам и операторам, приемам заказов и робота-техподдержки'
            go!: /general/general/agreement

        state: rejection
            intent: /ОТКАЗ
            audio: {{ audiofiles.rejection }} || name = 'Хорошо, я поняла вас. Если нужен будет голосовой или чат-бот, звоните. До свидания'
            go!: /general/general/agreement

        state: incomingChatBot
            intent: /ВХОДЯЩИЙ_ЧАТ_БОТ
            audio: {{ audiofiles.incomingChatBot }} || name = 'Мы можем сделать вам бота для приема заказов, отзывов или робота-техподдержки'
            go!: /general/general/agreement

        state: outgoingChatBot
            intent: /ИСХОДЯЩИЙ_ЧАТ_БОТ
            audio: {{ audiofiles.outgoingChatBot }} || name = 'Это может быть бот лидогенератор, оценщик CSI или же же просто бот уведомляющий клиентов об акциях, записях или времени начала мероприятия.'
            go!: /general/general/agreement

        state: allInclusive
            intent: /ВСЕ_ВМЕСТЕ
            audio: {{ audiofiles.allInclusive }} || name = 'Мы постараемся создать вас комплексное решение, которое будет отвечать всем требованиям вашего бизнеса'
            go!: /general/general/agreement

    state: chooseBot
        audio: {{ audiofiles.chooseBot }} || name = 'Подскажите какой бот смог бы помочь вам автоматизировать бизнес-процессы?'
        go!: /general/generalIntents