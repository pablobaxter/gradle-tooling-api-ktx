tasks.create<Delete>("clean") {
    delete(rootProject.layout.buildDirectory)
}

tasks.named<Wrapper>("wrapper") {
    distributionType = Wrapper.DistributionType.ALL
}
