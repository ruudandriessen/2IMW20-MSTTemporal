# Load data
data <- read.csv("./processedfinal.csv")


# setup window
par(mfrow=c(3,2))


# Plot graphs for algorithm running time
for (index in c(1, 2)) {
	#preprocess data by removing i == 1 by setting them into old/new sets
	new <- data[data$Algorithm!="Old",]
	old <- data[data$Algorithm!="New",]
	old <- old[old$i==index,]
	new <- new[new$i==index,]
	
	#old[7] and new[7] for normal time, old[9] and new[9] for sum time (includes post-P)
	all <- data.frame(old[2], old[3], old[9], new[9])

	all <- data.frame(all[1], all[2], all[,3:ncol(all)]/1000)

	# x = max |V| closure, y = max time
	xrangeV <- range(0, max(all[1]))
	xrangeEV <- range(min(all[2]/all[1]), max(all[2]/all[1]))
	yrange <- range(0, max(all[3], all[4]))

	colors = c('#00FF00', '#FF0000')

	all <- all[with(all, order(X.V..closure)),]
	
	png(filename=paste("V", index))
	# Plot |V| v.s. Time
	matplot(
		xlim=xrangeV, ylim=yrange, 
		type="l", col=colors,
		xlab="|V|", ylab="Time (s)", 
		all[,1], all[,3:ncol(all)]
	)
	dev.off();
	all <- all[with(all, order(X.E..closure/X.V..closure)),]
	
	png(filename=paste("EoverV", index))
	# Plot |E|/|V| v.s. time
	matplot(
		xlim=xrangeEV, ylim=yrange, 
		type="l", col=colors,
		xlab="|E|/|V|", ylab="Time (s)", 
		all[,2]/all[,1], all[,3:ncol(all)]
	)
	dev.off();
}

data <- data.frame(data[,1], data[,-1], (data[,9]/data[,10]))
diffData <- (data[,12]-mean(data[,12]));
diffRange <- range(min(diffData), max(diffData))
colors <- c('#000000', "#00FF00", "#FF0000")

png(filename="diffV.png")
plot(
	ylim=diffRange, type="p", pch="o",
	xlab="|V|", ylab="Difference(Theoretical-Experimental)",
	data[,2], diffData, col=data[,6]
)
abline(h=0, lty=2)
dev.off()

png(filename="diffE.png")
plot(
	ylim=diffRange, type="p", pch="o",
	xlab="|E|", ylab="Difference(Theoretical-Experimental)",
	data[,3]/data[,2], diffData, col=data[,6]
)
abline(h=0, lty=2)
dev.off()


