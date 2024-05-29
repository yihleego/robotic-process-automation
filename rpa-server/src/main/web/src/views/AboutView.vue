<template>
  <div>
    <h1>{{ $t("about.header") }}</h1>

    <p>{{ $t("about.donations", {donations: $n(1456, "currencyFormat")}) }}</p>
  </div>
</template>
