# Проект по расчету доступности графа

Есть интерфейс **Experiment**, который представляет собой, собственно, эксперимент.
На выходе эксперимента, самое главное набор точек для графика **aMax** и **aMin** от шага.

Запуск эксперимента осуществляется методом **Experiments.makeExperiment**, по результату строится три графика (кладутся в ту же папку):
+ количество узлов от шага (опционален)
+ количество связей от шага (опционален)
+ aMax и aMin от шага

Примеры самих экспериментов есть в **Experiments**, и запускаются в тесте **RunningExperiments** (просто для удобства)

В рамках эксперимента для расчета следующего шага (по сути следующей матрицы) используется интерфейс **Step**, который описывает действие следующего шага.
Выход из эксперимента осуществляется с помощью интерфейса **EndingCondition** или выборосм исключения **EarlyEndException** (рекомендуется если внутри шага мы поняли, что дальше нет смысла прогонять и можно выйти, должно обрабатываться в **Experiment**)

Для некоторых имплементации экспериментов есть также вариант передачи **MatrixGenerator**,для генерации матрицы, и **SpanningTreeCounter** для расчета остовного дерева.

Описание **Step**, в самих реализациях, но основной алгортим **AddingBestAmaxEdgeStep**, опыта наверное проводить на нем.

Есть отрисовка графа методом **GraphHelper.visualizeGraph**, но его надо тюнить для нормальной отрисовки.