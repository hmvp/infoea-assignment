c <- csv.get('test.csv', sep=';')
ils <- c[c$Heuristic=='ILS',]
gls <- c[c$Heuristic=='GLS',]
agls <- c[c$Heuristic=='AGLS',]

plot(Fitness~Index,data=ils, type='l')
lines(gls$Index,gls$Fitness, col=2)
lines(agls$Index,agls$Fitness, col=3)
